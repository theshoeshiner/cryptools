package org.thshsh.crypt.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.thshsh.crypt.ExchangeRepository;
import org.thshsh.crypt.HasImage;
import org.thshsh.crypt.cryptocompare.CryptoCompare;

@Service
@RestController
public class ImageService {

	public static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);

	@Autowired
	ExchangeRepository exRepo;

	//String imageArchive = "./media/images";
	File imageArchive = new File("../cryptools_media/images");


	@PostConstruct
	public void postConstruct() {
		//File archive = new File(imageArchive);
		if(!imageArchive.exists()) {
			imageArchive.mkdirs();
		}
	}

	@Autowired
	CryptoCompare compare;

	public void syncImage(HasImage entity, String mediaUrl) throws IOException {
		LOGGER.info("sync image: {}",entity);

		String imageName = entity.getKey().toLowerCase()+"."+FilenameUtils.getExtension(mediaUrl);

		if(entity.getImageUrl()!=null) {
			LOGGER.info("Checking for file: {}",entity.getImageUrl());
			File imageFile = new File(imageArchive,entity.getImageUrl());
			if(!imageFile.exists()) {
				//check for old file
				//String adjusted = mediaUrl.replaceAll(mediaUrl, imageName);
				File mediaUrlFile = new File(mediaUrl);
				File old = new File("./images/"+mediaUrlFile.getName());
				LOGGER.info("Checking for OLD file: {}",old);
				if(old.exists()) {
					//copy old file
					LOGGER.info("Copying OLD file");
					FileInputStream fis = new FileInputStream(old);
					FileOutputStream fos = new FileOutputStream(imageFile);
					IOUtils.copy(fis, fos);
					entity.setImageUrl(imageName);
				}
				else {
					LOGGER.info("Setting null");
					entity.setImageUrl(null);
				}
			}

		}

		if(entity.getImageUrl()==null) {
			File imageFile = new File(imageArchive,imageName);

			if(copyFromApi(mediaUrl, imageFile)) {
				LOGGER.info("saving image: {}",imageFile);
				entity.setImageUrl(imageName);
			}
		}

	}

	protected boolean copyFromApi(String mediaUrl,File archive) throws IOException {
		InputStream is = compare.getImage(mediaUrl);
		if(is != null) {
			FileOutputStream fos = new FileOutputStream(archive);
			IOUtils.copy(is, fos);
			return true;
		}
		return false;
	}

	public InputStream getImage(HasImage hi) throws FileNotFoundException {
		String imageName = hi.getImageUrl();
		return getImage(imageName);
	}

	public FileInputStream getImage(String imageName) throws FileNotFoundException {
		File imageFile = new File(imageArchive,imageName);
		FileInputStream fis = new FileInputStream(imageFile);
		return fis;
	}

	@GetMapping( value = "/image/{name}",
			 produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
	)
	public @ResponseBody ResponseEntity<?> getImageRequest(@PathVariable String name) throws IOException {
		//LOGGER.info("getimage: {}",name);
		FileInputStream is = getImage(name);
		HttpHeaders httpHeaders = new HttpHeaders();
		//LOGGER.info("is: {}",is);

		//return IOUtils.toByteArray(is);

		InputStreamResource inputStreamResource = new InputStreamResource(is);
		httpHeaders.setContentLength(is.available());
		return new ResponseEntity<>(inputStreamResource, httpHeaders, HttpStatus.OK);

		//InputStream in = getClass().getResourceAsStream("/com/baeldung/produceimage/data.txt");
		//return IOUtils.toByteArray(in);
		/*if(exchange != null) {
			Exchange e = exRepo.findByKey(exchange);
			InputStream is = getImage(e);
			return IOUtils.toByteArray(is);
		}
		return null;*/
	}


}
