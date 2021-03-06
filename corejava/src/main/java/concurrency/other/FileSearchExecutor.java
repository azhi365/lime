package concurrency.other;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Main class of the example. Create three FileSearch objects, encapsulate inside
 * three Task objects and execute them as they were callable objects
 */
public class FileSearchExecutor {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // Create a new Executor
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        // Create three FileSearch objects
        FileSearch system = new FileSearch("C:\\Windows", "log");
        FileSearch apps = new FileSearch("C:\\Program Files", "log");
        FileSearch documents = new FileSearch("C:\\Documents And Settings", "log");

        // Create three Task objects
        Task systemTask = new Task(system, null);
        Task appsTask = new Task(apps, null);
        Task documentsTask = new Task(documents, null);

        // Submit the Tasks to the Executor
        executor.submit(systemTask);
        executor.submit(appsTask);
        executor.submit(documentsTask);

        // Shutdown the executor and wait for the end of the tasks
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Write to the console the number of results
        try {
            System.out.printf("Main: System Task: Number of Results: %d\n", systemTask.get().size());
            System.out.printf("Main: App Task: Number of Results: %d\n", appsTask.get().size());
            System.out.printf("Main: Documents Task: Number of Results: %d\n", documentsTask.get().size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    /**
     * This class encapsulates a FileSearch object. The objective
     * is execute that task and returns the result that it generates
     * as it was a Callable object
     */
    public static class Task extends FutureTask<List<String>> {

        private FileSearch fileSearch;

        /**
         * Constructor of the class
         *
         * @param runnable FileSearh object that is going to execute
         * @param result   Object to return as result. We are going to ignore this structure
         */
        public Task(Runnable runnable, List<String> result) {
            super(runnable, result);
            this.fileSearch = (FileSearch) runnable;
        }

        /**
         * Override the set method. As we are going to execute a Runnable object, this
         * method establish the null value as result. We change this behavior returning
         * the result list generated by the FileSearch task
         */
        protected void set(List<String> v) {
            if (v == null) {
                v = fileSearch.getResults();
            }
            super.set(v);
        }
    }


    /**
     * This class search for files with an extension in a directory
     */
    public static class FileSearch implements Runnable {

        /**
         * Initial path for the search
         */
        private String initPath;

        /**
         * Extension of the file we are searching for
         */
        private String end;

        /**
         * List that stores the full path of the files that have the extension we are searching for
         */
        private List<String> results;


        /**
         * Constructor of the class. Initializes its attributes
         *
         * @param initPath Initial path for the search
         * @param end      Extension of the files we are searching for
         */
        public FileSearch(String initPath, String end) {
            this.initPath = initPath;
            this.end = end;
            results = new ArrayList<>();
        }

        /**
         * Method that returns the list or results
         *
         * @return
         */
        public List<String> getResults() {
            return results;
        }

        /**
         * Main method of the class. See the comments inside to a better description of it
         */
        @Override
        public void run() {

            System.out.printf("%s: Starting\n", Thread.currentThread().getName());

            // 1st Phase: Look for the files
            File file = new File(initPath);
            if (file.isDirectory()) {
                directoryProcess(file);
            }
        }

        /**
         * Method that process a directory
         *
         * @param file : Directory to process
         */
        private void directoryProcess(File file) {

            // Get the content of the directory
            File list[] = file.listFiles();
            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    if (list[i].isDirectory()) {
                        // If is a directory, process it
                        directoryProcess(list[i]);
                    } else {
                        // If is a file, process it
                        fileProcess(list[i]);
                    }
                }
            }
        }

        /**
         * Method that process a File
         *
         * @param file : File to process
         */
        private void fileProcess(File file) {
            if (file.getName().endsWith(end)) {
                results.add(file.getAbsolutePath());
            }
        }

    }

}
