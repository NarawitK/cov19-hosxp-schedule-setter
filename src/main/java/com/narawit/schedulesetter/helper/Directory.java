package com.narawit.schedulesetter.helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Directory {
	public static boolean checkDirectoryExists(Path path) {
		return Files.exists(path);
	}
	
	public static String CreateDirectory(Path path) throws IOException {
		Path dirPath =  Files.createDirectory(path);
		return dirPath.toString();
	}
}
