package file;



import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;

/**
 *  Watch a directory and all subdirectories for changes to files.
 */

public class DirectoryMonitor{// implements Runnable {

    private final WatchService watcher;
    public Map<WatchKey,Path> keys;
    private final boolean recursive;
    private boolean trace = false;
    private boolean newDir = false;
    public WatchKey key;
    
	private Vector<File> filesChanged  = new Vector<File>();
	private Vector<File> filesRemoved  = new Vector<File>();
	private String pathName;
	
	private long lastModifiedTime;

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }
    
    /**
     * Creates a WatchService and registers the given directory
     */
    public DirectoryMonitor(Path path, boolean recursive) throws IOException {
    	this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey,Path>();
        this.recursive = recursive;
        lastModifiedTime = System.currentTimeMillis();
        
        // if contains sub-directories, add all
        if (recursive) {
            System.out.format("Scanning %s ...\n", path);
            registerAll(path);
            System.out.println("Done.");
        } else {
            register(path);
        }

        // enable trace after initial registration
        this.trace = true;
        this.processEvents();
        
    }
    
    /**
     * @return a vector of all the modified or newly added files
     */
    public Vector<File> getFilesChanged() {
    	return filesChanged;
    }
    
    /**
     * @return all files that have to be removed
     */
    public Vector<File> getFilesRemoved(){
    	return filesRemoved;
    }
    
    /**
     * @return the time of the last modification 
     */
    synchronized public long getLastModTime(){
    	return lastModifiedTime;
    }
    
    /**
     * Clears the list of files that have been modified or deleted.
     * This method should be called after every upload.
     */
    public void clearVectors(){
    	filesChanged.clear();
    	filesRemoved.clear();
    }
    
    /**
     * Sets the path to the new file path
     * @param dir
     */
    public void setPath(String dir){
    	pathName = dir;
    }
    
    /**
     * Register the given directory with the WatchService
     */
    public void register(Path dir) throws IOException {
    	//Registers what events to listen for
       key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
       if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                System.out.format("register: %s\n", dir);
            } else {
                if (!dir.equals(prev)) {
                    System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException
            {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }


    /**
     * Process all events for keys queued to the watcher
     */
   public void processEvents() {
        for (;;) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event: key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();
                
                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }
                
                
                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                //If the files were newly created or modified then add to the vector.
                if(event.kind() == ENTRY_CREATE || event.kind() == ENTRY_MODIFY){
                	filesChanged.add(child.toFile());
                	lastModifiedTime = System.currentTimeMillis();
                }
                
                //If any file was deleted then add to the corresponding vector.
                if(event.kind() == ENTRY_DELETE){
                	lastModifiedTime = System.currentTimeMillis();
                	filesRemoved.add(child.toFile());
                }
                
                System.out.format("%s: %s\n", event.kind().name(), child);

                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (recursive && (kind == ENTRY_CREATE)) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {
                    }
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }


//    public void run() {
//    	//runs constantly and handles event through the process events method.
//        Path dir = Paths.get(pathName);
//        System.err.println("The path name is:" + pathName);
//        try {
//			new DirectoryMonitor(dir, recursive).processEvents();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//    }
    
    
}
