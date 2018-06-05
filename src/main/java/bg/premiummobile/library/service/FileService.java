package bg.premiummobile.library.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.tomcat.jni.Directory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.SynchronossPartHttpMessageReader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import bg.premiummobile.library.exception.ImageSaveException;

@Service
public class FileService {

	@Value("${bookImages.path:images}")
	private String path;
	
	@Value("${bookImages.fileExtension:jpg}")
	private String fileExtension;
	
	public String persistFile(MultipartFile inputFile, String imageName) throws ImageSaveException{
		imageName = imageName.replaceAll(" ", "-");
		StringBuilder st = new StringBuilder();
		st.append("src");
		st.append(File.separator);
		st.append("main");
		st.append(File.separator);
		st.append("resources");
		st.append(File.separator);
		st.append("static");
		st.append(File.separator);
		st.append(path);
		
		Path filePath = Paths.get(path);
		
		st.append(File.separator);
		st.append(imageName);
		st.append(".");
		st.append(fileExtension);
		
		File file = new File(st.toString());
		
		try {
			Files.createDirectories(filePath);
		} catch (IOException e1) {
			throw new ImageSaveException(e1.getMessage());
		}
		
		if(!file.exists() && !file.isDirectory()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new ImageSaveException(e.getMessage());
			}
		}

		try (FileOutputStream fop = new FileOutputStream(file)) {

			byte[] contentInBytes = inputFile.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();

		} catch (Exception e) {
			throw new ImageSaveException(e.getMessage());
		}
		st.setLength(0);
		st.append(path);
		st.append("\\");
		st.append(imageName);
		st.append(".");
		st.append(fileExtension);
		return st.toString();
	}
}
