package cin.ufpe.br.cecotool_transformer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.github.javaparser.ast.CompilationUnit;

public class ModifiedFile {
	private String absolutePath;
	private CompilationUnit compUnit;
	public ModifiedFile(String absolutePath, CompilationUnit compUnit) {
		this.absolutePath = absolutePath;
		this.compUnit = compUnit;
	}
	public CompilationUnit getCompUnit() {
		return this.compUnit;
	}
	public void apply() throws IOException {
		FileOutputStream out = new FileOutputStream(this.absolutePath);
    	OutputStreamWriter ow = new OutputStreamWriter(out);
    	ow.write(this.compUnit.toString());
    	ow.close();
    	out.close();
	}
}
