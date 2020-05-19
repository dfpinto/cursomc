package com.nelioalves.cursomc.services;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nelioalves.cursomc.services.exceptions.FileException;

@Service
public class ImageService {

	public BufferedImage getJpgImageFromFile(MultipartFile uploadedFile) {
		String ext = FilenameUtils.getExtension(uploadedFile.getOriginalFilename());
		ext = ext.toUpperCase();
		//System.out.println(ext);
		if(!"PNG".equals(ext) && !"JPG".equals(ext)) {
			throw new FileException("Somente imagens PNG e JPG são permitidas.");
		}
		
		try {
			BufferedImage img = ImageIO.read(uploadedFile.getInputStream());
			if("PNG".equals(ext)) {
				img = pngTojpg(img);
			}
			
			return img;
		} catch (IOException e) {
			throw new FileException("Erro ao ler o arquivo.");
		}
	}

	public BufferedImage pngTojpg(BufferedImage img) {
		
		BufferedImage jpgImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		// Preenche o fundo da imagem com branco.
		jpgImage.createGraphics().drawImage(img, 0, 0, Color.WHITE, null);
				
		return jpgImage;
	}
	
	// Converte a imagem para o tipo InputStream a ser usada pelo AWS
	public InputStream getInputStream(BufferedImage img, String ext) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(img,ext,os);
			return new ByteArrayInputStream(os.toByteArray());
		}
		catch(IOException e) {
			throw new FileException("Erro ao ler arquivo");
		}
	}
	
	public BufferedImage cropSquare(BufferedImage sourceImg) {
		int min = (sourceImg.getHeight() <= sourceImg.getWidth() ? sourceImg.getHeight() : sourceImg.getWidth());
		return Scalr.crop(sourceImg, 
				// coordenadas de corte
				(sourceImg.getWidth()/2)-(min/2), 
				(sourceImg.getHeight()/2)-(min/2), 
				min, 
				min);
	}
	
	public BufferedImage resize(BufferedImage sourceImg, int size) {
		return Scalr.resize(sourceImg, Scalr.Method.ULTRA_QUALITY, size);
	}
}
