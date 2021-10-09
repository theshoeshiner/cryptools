package org.thshsh.crypt.serv;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.repo.CurrencyRepository;

@Service
public class CurrencyService {

	public static final Logger LOGGER = LoggerFactory.getLogger(CurrencyService.class);

	File imagePath = new File("images");

	@Autowired
	CurrencyRepository currRepo;

	@PostConstruct
	public void postConstruct() {
		if(!imagePath.exists()) {
			imagePath.mkdir();
		}
	}


	public InputStream getImage(Currency c) throws IOException {
		if(c.getImageUrl()!=null) {
			File image = new File(imagePath,c.getImageUrl());
			if(image.exists()) {
				return new FileInputStream(image);
			}
		}
		return null;
	}

	public InputStream getImage(String name) throws IOException {
		File image = new File(imagePath,name);
		if(image.exists()) return new FileInputStream(image);
		else return null;
	}

	public void saveImage(Currency c,InputStream is,String name) throws IOException {
		LOGGER.info("Saving image: {}",name);

		File image = new File(imagePath,name);
		FileOutputStream fos = new FileOutputStream(image);
		IOUtils.copy(is, fos);
		c.setImageUrl(name);
	}

}
