import java.io.File;
import java.io.FilenameFilter;

public class PgnFileFetcher {

    private File[] files;

    public PgnFileFetcher() {
        File cwd = new File(".");
        files = cwd.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".pgn");
            }
        });
    }

    public File[] getFiles() {
        return files;
    }

    public void printFiles() {
        System.out.println("Files pulled...");
        for (File f : files) {
            System.out.println(f.getName());
        }
    }

}
