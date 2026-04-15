package it.aulab.progetto_finale_java.services;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import it.aulab.progetto_finale_java.dtos.ArticleDto;
import it.aulab.progetto_finale_java.models.Article;
import it.aulab.progetto_finale_java.models.Category;
import it.aulab.progetto_finale_java.models.User;
import it.aulab.progetto_finale_java.repositories.ArticleRepository;
import it.aulab.progetto_finale_java.repositories.UserRepository;

@Service
public class ArticleService implements CrudService<ArticleDto, Article, Long>{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ImageService imageService;

    @Override
    public List<ArticleDto> readAll() {
        List<ArticleDto> dtos = new ArrayList<ArticleDto>();
        for(Article article: articleRepository.findAll()){
            dtos.add(modelMapper.map(article, ArticleDto.class));
        }
        return dtos;
    }
    @Override
    public ArticleDto read(Long key) {
        Optional<Article> opArticle = articleRepository.findById(key);
        if(opArticle.isPresent()){
            return modelMapper.map(opArticle.get(), ArticleDto.class);
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Author id=" + key + "not found");
        }
    }
    @Override
    public ArticleDto create(Article article, Principal principal, MultipartFile file) {
        String url = "";

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            CustomUserDetails userDetails =(CustomUserDetails) authentication.getPrincipal();
            User user = (userRepository.findById(userDetails.getId())).get();
            article.setUser(user);
        }

        if(!file.isEmpty()){
            try{
                CompletableFuture<String> futureUrl = imageService.saveImageOnCloud(file);
                url= futureUrl.get();
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        article.setIsAccepted(null);

        ArticleDto dto = modelMapper.map(articleRepository.save(article), ArticleDto.class);
       
        if(!file.isEmpty()){
            imageService.saveImageOnDB(url,article);
        }
        return dto;
    }

    @Override
    public ArticleDto update(Long key, Article updatedArticle, MultipartFile file) {
        String url="";

        // controllo l'eistenza di un articolo in base al suo id 
        if(articleRepository.existsById(key)){
            // assegno all'articolo proveniente dal form lo stesso id dell'articolo originale
            updatedArticle.setId(key);
            // recupero articolo originale non modificato
            Article article = articleRepository.findById(key).get();
            // imposto l' utente articolo del form con l' utente dll'articolo originale 
            updatedArticle.setUser(article.getUser());
            // faccio controllo su presenza o meno del file nell'articolo del form e capisco se devo modificare o meno l'immagine 
            if(!file.isEmpty()){
                // elimino immagine precedente 
                try{

                    imageService.deletImage(article.getImage().getPath());
                    try{
                        // salvo la nuova immagine
                        CompletableFuture<String> futureUrl = imageService.saveImageOnCloud(file);
                        url = futureUrl.get();
                    }catch(Exception e ){
                        e.printStackTrace();
                    }
                    // salvo nuovo path nel db

                    imageService.saveImageOnDB(url, updatedArticle);
                    // essendo l'immagine modificata l'articolo torna in revisione
                    updatedArticle.setIsAccepted(null);
                    return modelMapper.map(articleRepository.save(updatedArticle), ArticleDto.class);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }else if (article.getImage()== null) {
                // se l'articolo originale non ha un'immagine e neanche quello da modificare allora non è stata fatta nessuna modiifca 
                updatedArticle.setIsAccepted(article.getIsAccepted());
                
            }else{
                // se l'immagine non è stata modificata devo fare check sugli altri campi se diversi, l'articolo torna in revisione
                // se l'immagine dell'articolo originale non è stata modificata posso impostare la stessa immagine sull'articolo modificato
                updatedArticle.setImage(article.getImage());

                if(updatedArticle.equals(article)== false){
                    updatedArticle.setIsAccepted(null);
                }else{
                    updatedArticle.setIsAccepted(article.getIsAccepted());
                }
                return modelMapper.map(articleRepository.save(updatedArticle), ArticleDto.class);
            }
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return null;
    }


    @Override
    public void delete(Long key) {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
        
    }

    public List<ArticleDto> searchByCategory(Category category){
        List<ArticleDto> dtos = new ArrayList<ArticleDto>();
        for(Article article : articleRepository.findByCategory(category)){
            dtos.add(modelMapper.map(article, ArticleDto.class));
        }
        return dtos;
    }

    public List<ArticleDto> searchByAuthor(User user){
        List<ArticleDto> dtos = new ArrayList<ArticleDto>();
        for(Article article : articleRepository.findByUser(user)){
            dtos.add(modelMapper.map(article, ArticleDto.class));
        }
        return dtos;
    }

    public void setIsAccepted(Boolean result, Long id){
        Article article = articleRepository.findById(id).get();
        article.setIsAccepted(result);
        articleRepository.save(article);
    }

    public List<ArticleDto> search(String keyword){
        List<ArticleDto> dtos = new ArrayList<ArticleDto>();
        for(Article article : articleRepository.search(keyword)){
            dtos.add(modelMapper.map(article, ArticleDto.class));
        }
        return dtos;
    }


}
