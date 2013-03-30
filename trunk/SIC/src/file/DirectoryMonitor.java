package file;

/**
 * The DirectoryMonitor scans a directory and reports
 * whenever a file within the directory has been modified
 * 
 * TODO: make this tie into the rest of the program
 * 
 */

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public class DirectoryMonitor implements Runnable{

	private final WatchService watcher;
	private final Map<WatchKey,Path> keys;
	

	public void registerDir(Path dir) throws IOException {
		SimpleFileVisitor<Path> dirScanner = new SimpleFileVisitor<Path>() {
			@Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                registerFile(dir);
                return FileVisitResult.CONTINUE;
            }
		};
		
		
		Files.walkFileTree(dir, dirScanner);
	}

	public void registerFile(Path file) throws IOException {
		WatchKey key = file.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

		keys.put(key, file);
	}


	public DirectoryMonitor() throws IOException {
		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<WatchKey,Path>();
	}

	@SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }
	
	@Override
	public void run() {
		WatchKey key = null;
		
		while (true) {
			
            try {
                key = watcher.take();
            } catch (InterruptedException e) {
                return;
            }

            Path dir = keys.get(key);
//            if (dir == null) {
//                System.err.println("WatchKey not recognized!!");
//                continue;
//            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();
                
                if (kind == OVERFLOW) {
                    continue;
                }
             
                
                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                // print out event
                System.out.format("%s: %s\n", event.kind().name(), child);

                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (kind == ENTRY_CREATE) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerDir(child);
                        }
                    } catch (IOException e) {
                    	
                    }
                }
            }
		}

	}

}
