import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class GenHtml {

	private String writeDir, readDir;

	public GenHtml() {
		writeDir = "/home/arya/Resources_FOEVS_ATG";
		new File(writeDir).mkdirs();
		readDir = "/home/arya/EclipseProjects/Foevs_Servlet/WebContent/Resources_FOEVS_ATG";
	}

	public static void main(String args[]) throws IOException {
		GenHtml gen = new GenHtml();
		gen.run();
	}

	private void run() throws IOException {

		File[] files1 = new File(readDir).listFiles();
		for (int i1 = 0; i1 < files1.length; i1++) {
			String n1 = files1[i1].getName();
			if (files1[i1].isDirectory()) {
				new File(n1).mkdir();

				File[] files2 = files1[i1].listFiles();
				for (int i2 = 0; i2 < files2.length; i2++) {
					String n2 = files2[i2].getName();// at level 2, all folders
					new File(n1 + "/" + n2).mkdir();

					File[] files3 = files2[i2].listFiles();
					for (int i3 = 0; i3 < files3.length; i3++) {
						String n3 = files3[i3].getName();// at level 2, all files

						if (n3.equals("data")) {
							createWebpage(n1 + "/" + n2 + "/" + n3, getPrintObject(n1 + "/" + n2 + "/" + "info.html"));
						} else
							copyFile(files3[i3].getAbsolutePath(), n1 + "/" + n2 + "/" + n3);
					}
				}
			} else {
				if (n1.equals("data")) {
					createWebpage(n1, getPrintObject("info.html"));
				} else
					copyFile(files1[i1].getAbsolutePath(), n1);
			}
		}
	}

	private void copyFile(String src, String dest) throws IOException {
		InputStream is = new FileInputStream(src);
		OutputStream os = new FileOutputStream(dest);

		byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
	}

	private PrintWriter getPrintObject(String path) throws IOException {
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(path)));
		return pw;
	}

	private void createWebpage(String path, PrintWriter pw) throws IOException {
		printData(readFun("top_forhtml"), pw, path);
		printData(readFun(path), pw, path);
		printData(readFun("bottom_forhtml"), pw, path);
		pw.close();
	}

	private BufferedReader readFun(String path) throws IOException {
		path = readDir + "/" + path;
		BufferedReader br = new BufferedReader(new FileReader(path));
		return br;
	}

	private void printData(BufferedReader br, PrintWriter sw, String path) throws IOException {
		path = path.substring(0, path.length() - 4);// remove "data" file
		for (;;) {
			String st = br.readLine();
			if (st == null)
				break;

			if (st.startsWith("\\takefrom")) {
				takeFrom(st, sw);
				continue;
			} else if (st.startsWith("\\image")) {
				st = st.substring(7);
				st = path + st;// path already contains "/" at the end
				sw.println("<img class=image src=\"" + st + "\" style=\"width: 300px; text-align: center;\">");
				continue;
			}
			sw.println(st);
		}
	}

	private void takeFrom(String st, PrintWriter sw) throws IOException {
		st = st.substring(9);
		if (st.equals("chapterMenu")) {
			takeFromChapterMenu(st, sw);
		}
	}

	private void takeFromChapterMenu(String st, PrintWriter sw) throws IOException {
		BufferedReader br = readFun(st);
		int tab = -1;
		int row = 0;
		String unit = "";
		for (;;) {
			String a = br.readLine();

			if (a == null) {
				sw.println("</ul>\n</li>\n</ul>");
				break;
			}
			// a=a.toUpperCase();

			if (a.startsWith("\t")) {
				a = a.substring(1);
				if (tab == 0) {
					sw.println("<ul>");
					tab = 1;
				}
				sw.println("<li><a href=\"/" + unit.replaceAll("\\s", "") + "/" + a.replaceAll("\\s", "") + "/info.html" + "\">" + a
						+ "</a></li>");
			} else {
				unit = a;
				if (tab == -1) {
					sw.println("<ul>");
					tab = 0;
				} else if (tab == 1) {
					sw.println("</ul>\n</li>");
					tab = 0;
				}
				sw.println("<li><a href=\"?\">" + a + " &#9656</a>");
			}
		}
	}
}