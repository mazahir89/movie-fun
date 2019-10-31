package org.superbiz.moviefun.albums;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.blobstore.Blob;
import org.superbiz.moviefun.blobstore.BlobStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

@Controller
@RequestMapping("/albums")
public class AlbumsController {

    private BlobStore blobStore;
    private final AlbumsBean albumsBean;

    public AlbumsController(AlbumsBean albumsBean, BlobStore blobStore) {
        this.albumsBean = albumsBean;
        this.blobStore = blobStore;
    }


    @GetMapping
    public String index(Map<String, Object> model) {
        model.put("albums", albumsBean.getAlbums());
        return "albums";
    }

    @GetMapping("/{albumId}")
    public String details(@PathVariable long albumId, Map<String, Object> model) {
        model.put("album", albumsBean.find(albumId));
        return "albumDetails";
    }

    @PostMapping("/{albumId}/cover")
    public String uploadCover(@PathVariable long albumId, @RequestParam("file") MultipartFile uploadedFile) throws IOException {
        Blob blob = new Blob(String.valueOf(albumId), uploadedFile.getInputStream(), uploadedFile.getContentType());
        blobStore.put(blob);
        return format("redirect:/albums/%d", albumId);
    }

    @GetMapping("/{albumId}/cover")
    public HttpEntity<byte[]> getCover(@PathVariable long albumId) throws IOException, URISyntaxException {
        Optional<Blob> blob = blobStore.get(String.valueOf(albumId));

        byte[] imageBytes = {};
        HttpHeaders headers = new HttpHeaders();
        if (blob.isPresent()) {
            Blob blob1 = blob.get();
            imageBytes = copyBytes(blob1.inputStream);
            headers.setContentType(MediaType.parseMediaType(blob1.contentType));
            headers.setContentLength(imageBytes.length);
        } else {
            imageBytes = Files.readAllBytes(Paths.get(this.getClass().getClassLoader().getResource("default-cover.jpg").toURI()));
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(imageBytes.length);
        }

        return new HttpEntity<>(imageBytes, headers);
    }


    private byte[] copyBytes(InputStream inputStream) throws IOException {
        byte[] buff = new byte[8192];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int bytesRead;
        while ((bytesRead = inputStream.read(buff)) != -1) {
            baos.write(buff, 0, bytesRead);
        }
        return baos.toByteArray();
    }
}
