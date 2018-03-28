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
import java.util.Vector;

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
					createWebpage(n1, getPrintObject("index.html"));
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
				sw.println("<p style=\"text-align: center\"><img style=\"height: 300px\" src=\"" + st + "\">");
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

	int row = 1;
	private void takeFromChapterMenu(String st, PrintWriter sw) throws IOException {
		BufferedReader br = readFun(st);
		int tab = -1;
		String unit = "";

		Vector<String> chapters = new Vector<String>();

		for (;;) {
			String a = br.readLine();

			if (a == null) {
				if (tab == 2)
					writeChapters(sw, chapters);

				sw.println("</tbody>");
				sw.println("</table>");
				sw.println("<p><marquee><i><b>Click on the name of a chaper to continue.</i></b></marquee>");
				sw.println("<p>");
				break;
			}

			if (a.startsWith("\t\t")) {
				a = a.substring(2);
				chapters.add(a);
				tab = 2;
			} else if (a.startsWith("\t")) {
				a = a.substring(1);

				if (tab == 2)
					writeChapters(sw, chapters);
				
				tab = 1;
				sw.println("<tr>");
				sw.println("<td>"+row++);
				sw.println("<td rowspan=");
				chapters.add(a);
			} else {
				if (tab == -1) {
					sw.println("<p><marquee><i><b>Click on the name of a chaper to continue.</i></b></marquee>");
					sw.println("<p>");
					sw.println("<h2 style=\"text-align: center\">" + a + "</h2>");
					sw.println(
							"<table border=1 style=\"margin-right: 100px;margin-left: 100px;border-collapse:collapse\">\n"
									+ "	<tbody>\n"
									+ "		<tr>\n"
									+ "			<th>S. NO.\n"
									+ "			<th>UNIT\n"
									+ "			<th>CHAPTER");
				} else {
					if (tab == 2) {
						writeChapters(sw, chapters);
					}
					
					sw.println("\t</tbody>" + "</table>\n");
					sw.println("<p><marquee><i><b>Click on the name of a chaper to continue.</i></b></marquee>");
					sw.println("<p>");
					sw.println("<h2 style=\"text-align: center\">" + a + "</h2>");
					sw.println(
							"<table border=1 style=\"margin-right: 100px;margin-left: 100px;border-collapse:collapse\">\n"
									+ "	<tbody>\n" 
									+ "		<tr>\n"
									+ "			<th>S. NO.\n"
									+ "			<th>UNIT\n"
									+ "			<th>CHAPTER");
				}
				tab = 0;
				row=1;
			}
		}
	}

	private void writeChapters(PrintWriter sw, Vector<String>chapters) {
		sw.print(chapters.size() - 1 + ">");
		sw.println(chapters.get(0));
		String unit = chapters.get(0).replaceAll(" ", "");
		sw.print("<td>");
		sw.println("<a href=\"\\" + unit + "/"
				+ chapters.get(1).replaceAll(" ", "") + "/info.html\">"
				+ chapters.get(1)
				+ "</a>");

		for (int i = 2; i < chapters.size(); i++) {
			sw.println("<tr>");
			sw.println("<td>" + (row++));
			sw.println("<td>" + "<a href=\"\\" + unit + "/"
					+ chapters.get(i).replaceAll(" ", "") + "/info.html\">"
					+ chapters.get(i)
					+ "</a>");
		}
		chapters.removeAllElements();
	}
}