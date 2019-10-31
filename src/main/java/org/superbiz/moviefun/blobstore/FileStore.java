package org.superbiz.moviefun.blobstore;

import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Optional;

import static java.lang.String.format;

@Component
public class FileStore implements BlobStore {

//    public static void main(String[] args) throws IOException {
//        FileInputStream input = new FileInputStream(new File("hello.txt"));
//        Blob blob = new Blob("hello", input, "text/plain");
//        FileStore fs = new FileStore();
//        fs.put(blob);
//
//        Optional<Blob> optionalBlob = fs.get("hello");
//        System.out.println(optionalBlob.get().contentType);
//        if (fs.get("nothing").isPresent()) {
//            System.out.println("WHat?");
//        }
//        else {
//            System.out.println("OK!");
//        }
//    }

    @Override
    public void put(Blob blob) throws IOException {
        String coverFileName = format("covers/%s", blob.name);

        byte[] buffer = new byte[8 * 1024];

        int bytesRead;


        File targetFile = new File(coverFileName);

        OutputStream outStream = new FileOutputStream(targetFile);

        while ((bytesRead = blob.inputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);

        }
        outStream.close();
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        String coverFileName = format("covers/%s", name);


        File file = new File(coverFileName);
        Optional<Blob> optionalBlob;


        if (file.exists()) {
            InputStream inputStream = new FileInputStream(file);
            String contentType = new Tika().detect(coverFileName);
            Blob blob = new Blob(name, inputStream, contentType);
            return Optional.of(blob);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void deleteAll() {
        // ...
    }
}