package com.narawit.schedulesetter.helper;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.narawit.schedulesetter.Main;

public class DbProperties {
    static Path baseDir = Paths.get(System.getProperty("user.dir")).resolve("runtime");
    static Path configPath = baseDir.resolve("conf");
    static String dbPropertiesPath = configPath.resolve("db.properties").toString();

    public static void WriteDefaultDbProperties() throws IOException{
        // Create Directories where Properties will be created.
    	if(!Directory.checkDirectoryExists(baseDir)) {
    		Directory.CreateDirectory(baseDir);
            if(!Directory.checkDirectoryExists(configPath))
    		    Directory.CreateDirectory(configPath);
    	}
        File f = new File(dbPropertiesPath);
        if (!f.isFile()){
        	Properties props = new Properties();
        	try(InputStream is = Main.class.getResourceAsStream("defaultdb.properties")){
            	props.load(is);
            	try(OutputStream outputStream = new FileOutputStream(dbPropertiesPath)){
            		props.store(outputStream,"Default DB Properties");            		
            	}
        	}
        }
    }
    
    public static FileInputStream GetDbPropertiesAsInputStream() throws IOException {
        try{
            FileInputStream fStream = new FileInputStream(dbPropertiesPath);
            return fStream;
        }
        catch (IOException e){
        	throw new IOException("Cannot get Database Properties at this path: " + dbPropertiesPath);
        }
    }

    public static FileOutputStream GetDbPropertiesAsOutputStream() throws IOException{
        try{
            FileOutputStream fStream = new FileOutputStream(dbPropertiesPath);
            return fStream;
        }
        catch (IOException e){        
        	throw new IOException("Cannot output Database Properties at this path: " + dbPropertiesPath);          
        }
    }
}
