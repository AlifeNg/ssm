package com.wuying.ssm.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MyGenerate {

	private static String className = "User";

	private static String packageNameService = "com.wuying.ssm.service";
	private static String packageNameServiceImpl = "com.wuying.ssm.service.impl";
	private static String packageNameModel = "com.wuying.ssm.model";
	private static String packageNameController = "com.wuying.ssm.controller";
	private static String javaProject = "src/main/java";


	public static void getService() {
		try {
			StringBuffer header = new StringBuffer();
			StringBuffer footer = new StringBuffer();
			StringBuffer contentBuffer = new StringBuffer();
			header.append("package " + packageNameService + ";\n\n");
			header.append("import " + packageNameModel + "." + className + ";\n\n");
			contentBuffer.append("public interface " + className + "Service extends BaseService<" + className + "> {\n\n");
			footer.append("}");
			header.append(contentBuffer).append(footer);
			
			outputToFile(packageNameService, (className + "Service.java"), header.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void getServiceImpl() {
		try {
			StringBuffer header = new StringBuffer();
			StringBuffer footer = new StringBuffer();
			StringBuffer contentBuffer = new StringBuffer();
			header.append("package " + packageNameServiceImpl + ";\n\n");
			header.append("import org.apache.log4j.Logger;\n");
			header.append("import org.springframework.stereotype.Service;\n\n");
			header.append("import " + packageNameModel + "." + className + ";\n");
			header.append("import " + packageNameService + "." + className + "Service;\n\n");
			header.append("@Service\n");
			contentBuffer.append("public class " + className + "ServiceImpl extends BaseServiceImpl<" + className + "> implements "+className+"Service {\n\n");
			contentBuffer.append("\tprivate Logger logger = Logger.getLogger(this.getClass().getName());\n\n");
			footer.append("}");
			header.append(contentBuffer).append(footer);
			
			outputToFile(packageNameServiceImpl, (className + "ServiceImpl.java"), header.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void getController() {
		try {
			StringBuffer header = new StringBuffer();
			StringBuffer footer = new StringBuffer();
			StringBuffer contentBuffer = new StringBuffer();
			header.append("package " + packageNameController + ";\n\n");
			header.append("import " + packageNameService + "." + className + "Service;\n");
			header.append("import org.apache.log4j.Logger;\n");
			header.append("import org.springframework.beans.factory.annotation.Autowired;\n");
			header.append("import org.springframework.stereotype.Controller;\n");
			header.append("import org.springframework.web.bind.annotation.RequestMapping;\n\n");
			
			header.append("@Controller\n");
			header.append("@RequestMapping(\"/"+ firstWordLowerCase(className) +"\")\n");
			contentBuffer.append("public class " + className + "Controller {\n\n");
			contentBuffer.append("\tprivate Logger logger = Logger.getLogger(this.getClass().getName());\n\n");
			contentBuffer.append("\t@Autowired\n");
			contentBuffer.append("\tprivate "+ className + "Service " + firstWordLowerCase(className) + "Service;\n\n");
			footer.append("}");
			header.append(contentBuffer).append(footer);
			
			outputToFile(packageNameController, (className + "Controller.java"), header.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void outputToFile(String packageName, String fileName, String content) {
		String path = "";
		if(System.getProperty("os.name").indexOf("Mac") >= 0){
			 path = System.getProperty("user.dir") + "/" +  javaProject + "/" + packageName.replace(".", "/") + "/";
		} else {
			path = System.getProperty("user.dir") + "\\" +  javaProject + "\\" + packageName.replace(".", "\\") + "\\";
		}
        OutputStream os = null;
        try {    
            os = new FileOutputStream(path+fileName);    
        } catch (FileNotFoundException e1) {    
            e1.printStackTrace();    
        }    
        byte[] b = content.getBytes();    
        try {    
            os.write(b);    
            os.flush();    
        } catch (IOException e) {    
            e.printStackTrace();    
        } finally {    
            try {    
                os.close();    
            } catch (IOException e) {    
                e.printStackTrace();    
            }    
        }    
    }

	/**
	 * 首字母小写
	 * @param str
	 * @return
	 */
	public static String firstWordLowerCase(String str) {
		char[] cs = str.toCharArray();
		cs[0] += 32;
		return String.valueOf(cs);

	}
	public static void main(String[] args) {
		getService();
		getServiceImpl();
		getController();
	}
}
;