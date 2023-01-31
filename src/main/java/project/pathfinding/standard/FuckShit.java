package project.pathfinding.standard;

import java.io.File;
import java.util.LinkedList;

public class FuckShit {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File f=new File("site.html");
		LinkedList<File> files=new LinkedList();
		//files.add(f);
		//files.add(new File("/site.html"));
		files.add(new File("web/site.html"));
		//files.add(new File("/web/site.html"));
		files.add(new File("./web/site.html"));
		//files.add(new File("../web/site.html"));
		files.add(new File("src/main/resources/web/site.html"));
		//files.add(new File("/src/main/resources/web/site.html"));
		files.add(new File("./src/main/resources/web/site.html"));
		files.forEach((e)->{
			System.out.println(e.exists());
					});
		//System.out.println(f.exists());
	}

}
