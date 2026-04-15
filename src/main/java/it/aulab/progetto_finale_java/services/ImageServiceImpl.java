package it.aulab.progetto_finale_java.services;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import it.aulab.progetto_finale_java.models.Article;
import it.aulab.progetto_finale_java.models.Image;
import it.aulab.progetto_finale_java.repositories.ImageRepository;
import it.aulab.progetto_finale_java.utils.StringManipulation;

import org.springframework.http.*;
import jakarta.transaction.Transactional;



@Service
public class ImageServiceImpl implements ImageService{

    @Autowired
    private ImageRepository imageRepository;
    
    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String supabaseBucket;

    @Value("${supabase.image}")
    private String supabaseImage;

    private final RestTemplate restTemplate = new RestTemplate();

    public void saveImageOnDB(String url, Article article){
        url = url.replace(supabaseBucket, supabaseImage);
        imageRepository.save(Image.builder().path(url).article(article).build());
    }

    @Async
    public CompletableFuture<String> saveImageOnCloud(MultipartFile file) throws Exception{
        if(!file.isEmpty()){
            try{
                String nameFile= UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

                String extension= StringManipulation.getFileExtension(nameFile);

                String url= supabaseUrl + supabaseBucket + nameFile;

                MultiValueMap<String, Object> body= new LinkedMultiValueMap<>();

                body.add("file", file.getBytes());

                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", "image/" + extension);
                headers.set("Authorization", "Bearer" + supabaseKey);

                HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(),headers);

                restTemplate.exchange(url, HttpMethod.POST,requestEntity,String.class);

                return CompletableFuture.completedFuture(url);
            }catch(Exception e){
                e.printStackTrace();
            }
        }else{
            throw new IllegalArgumentException("File is empty");
        }
        return CompletableFuture.failedFuture(null);

    }
    
    @Async
    @Transactional
    public void deleteImage(String imagePath) throws IOException{
        String url = imagePath.replace(supabaseImage, supabaseBucket);

        imageRepository.deleteByPath(imagePath);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization" , "Bearer" + supabaseKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);

        System.out.println(response.getBody());
    }

    @Override
    public void deletImage(String imagePath) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deletImage'");
    }
    
}
