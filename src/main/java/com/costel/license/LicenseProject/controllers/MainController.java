package com.costel.license.LicenseProject.controllers;

import com.costel.license.LicenseProject.LicenseProjectApplication;
import com.costel.license.LicenseProject.mappers.*;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import parsers.Item;
import parsers.Site;
import parsers.XMLParser;
import tables.Evaluation;
import tables.News;
import tables.User;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Controller
public class MainController {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private EvaluationMapper evaluationMapper;


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String slash()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth!= null)
        {
            if(auth.getName().equals("anonymousUser"))
            {
                return "/login";
            }
            else
            {
                return "redirect:/news?rss=https://www.activenews.ro/rss";
            }
        }
        return "redirect:/news";
    }



    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null)
        {
            if(auth.getName().equals("anonymousUser"))
            {
                //este conectat ca anonim, adica nu este conectat
                model.addAttribute("userErrors", true);
                return "/login";
            }
            else
            {
                return "redirect:/news?rss=rss=https://www.activenews.ro/rss";
            }
        }
        else
        {
            System.out.println("Nu este autentificat!\n");//nu intra niciodata
        }

        return "/login";
    }


    @RequestMapping(value = "/loginn", method = RequestMethod.POST)
    public String login(
            @RequestParam(name="username") String username,
            @RequestParam(name="password") String password,
            Model model
    )
    {
        MongoCollection<org.bson.Document> usersCollection = LicenseProjectApplication.usersCollection;
        Document userFromMongoDB = usersCollection.find(eq("username", username)).first();

        if(userFromMongoDB != null){
            if(!userFromMongoDB.getString("password").equals(password)){
                model.addAttribute("paramError", true);
                return "/login";
            }else{
                List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
                if(username.equals("costelcroitoru")){
                    grantedAuths.add(new SimpleGrantedAuthority("ADMIN"));
                }else{
                    grantedAuths.add(new SimpleGrantedAuthority("USER"));
                }

                Authentication setAuth = new UsernamePasswordAuthenticationToken(userFromMongoDB.getString("username"),
                        userFromMongoDB.getString("password"), grantedAuths);
                SecurityContextHolder.getContext().setAuthentication(setAuth);

                Authentication getAuth = SecurityContextHolder.getContext().getAuthentication();

                if(getAuth != null)	//authenticate was created successfully
                {
                    //System.out.println("M-am logat!");
                    return "redirect:/news?rss=https://www.activenews.ro/rss";
                }
            }

        }else{
            model.addAttribute("paramError", true);
        }



        //================================== ma loghez daca user-ul exista in H2 DB====================================
//        User user = userMapper.getByUsername(username);
//
//        if(user != null)
//        {
//            if(!user.getPassword().equals(password))
//            {
//                model.addAttribute("paramError", true);
//                return "/login";
//            }
//            else
//            {
//                List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
//
//                if(username.equals("costelcroitoru")){
//                    grantedAuths.add(new SimpleGrantedAuthority("ADMIN"));
//                }
//                else{
//                    grantedAuths.add(new SimpleGrantedAuthority("USER"));
//                }
//
//
//                Authentication setAuth = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), grantedAuths);
//                SecurityContextHolder.getContext().setAuthentication(setAuth);
//
//
//                Authentication getAuth = SecurityContextHolder.getContext().getAuthentication();
//
//                if(getAuth != null)	//authenticate was created successfully
//                {
//					/*System.out.println("Nume: " + getAuth.getName());
//					System.out.println("Autoritate: " + getAuth.getAuthorities());
//					System.out.println("User: "+ getAuth.getPrincipal());
//					System.out.println("Parola: " + getAuth.getCredentials());
//					System.out.println("Detalii: " + getAuth.getDetails());
//					*/
//                    return "redirect:/news?rss=https://www.activenews.ro/rss";
//                }
//            }
//        }
//        else
//        {
//            model.addAttribute("paramError", true);
//        }
        //==========================================================================================================
        return "/login";
    }

    private static boolean checkInputValues(Model model, String name, String username, String password, String confirmPassword)
    {
        boolean hasErrors = false;
        if(name.length() < 3)
        {
            model.addAttribute("nameErrors", true);
            model.addAttribute("name", "Try one with at least 3 characters.");
            hasErrors = true;
        }
        if(username.length() < 5)
        {
            model.addAttribute("usernameErrors", true);
            model.addAttribute("username", "Try one with at least 5 characters.");
            hasErrors = true;
        }
        if(password.length() < 5)
        {
            model.addAttribute("passwordErrors", true);
            model.addAttribute("password", "Try one with at least 5 characters.");
            hasErrors = true;
        }
        if(confirmPassword.length() < 5)
        {
            model.addAttribute("confirmPasswordErrors", true);
            model.addAttribute("confirmPassword", "Try one with at least 5 characters.");
            hasErrors = true;
        }
        if(!password.equals(confirmPassword))
        {
            model.addAttribute("confirmPasswordErrors", true);
            model.addAttribute("confirmPassword", "This password doesn't match.");
            hasErrors = true;
        }
        return hasErrors;
    }


    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(
            @RequestParam(name="name") String name,
            @RequestParam(name="username") String username,
            @RequestParam(name="password") String password,
            @RequestParam(name="confirm-password") String confirm_password,
            Model model
    )
    {

        boolean hasInputErrors = checkInputValues(model, name, username, password, confirm_password);


        if(hasInputErrors)
        {
            model.addAttribute("register", true);
            return "/login";
        }
        else
        {
            //====================== adaug in Mongo ========================================================
            MongoCollection<org.bson.Document> usersCollection = LicenseProjectApplication.usersCollection;
            Document userFromMongoDB = usersCollection.find(eq("username", username)).first();
            if(userFromMongoDB != null){    //daca utilizatorul exista
                model.addAttribute("register", true);
                return "/login";
            }

            ArrayList<Object> idObjects = new ArrayList<>();

            Document userDocument = new Document("name", name)
                    .append("username", username)
                    .append("password", password)
                    .append("role", "USER")
                    .append("newsVoted", idObjects);

            usersCollection.insertOne(userDocument);
            //System.out.println("M-am inregistrat!");
            //==============================================================================================


            //=====================adaug si in H2 DB =======================================================
            /*
            User userFromDB = userMapper.getByUsername(username);

            if(userFromDB != null)	//daca utilizatorul exista
            {
                model.addAttribute("register", true);
                return "/login";
            }

            User user = new User();
            user.setId(userMapper.getIdMax() + 1);
            user.setName(name);
            user.setUsername(username);
            user.setPassword(password);
            user.setRole("USER");

            try{
                userMapper.insert(user);

					*//*AuthenticationManagerBuilder authenticationManagerBuilder = new AuthenticationManagerBuilder(null);
					WebSecurityConfig webSecurityConfig = new WebSecurityConfig();
									  webSecurityConfig.userMapper = userMapper;
									  webSecurityConfig.configureGlobal(authenticationManagerBuilder);
					*//*
            }catch(Exception e){
                e.printStackTrace();
            }

            ===================================================================================================
            */
        }


        return "redirect:/login";
    }



    @RequestMapping(value = "/news", method = RequestMethod.GET)
    public String news(@RequestParam(name="rss") String link, Model model)
    {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        MongoCollection<org.bson.Document> usersCollection = LicenseProjectApplication.usersCollection;
        Document userFromMongoDB = usersCollection.find(eq("username", username)).first();  //user din mongo

        //User user = userMapper.getByUsername(username); //user din h2

        MongoCollection<Document> newsCollection = LicenseProjectApplication.newsCollection;

        //preiau documentul care are link-ul trimis ca parametru
        //Document newsFromDB = newsCollection.find(new Document("link", link)).first();

        ArrayList<Object> newsVotedByUser = (ArrayList<Object>) userFromMongoDB.get("newsVoted");

        ArrayList<Site> sites = XMLParser.getAllSitesFromXML("newsSites.xml");

        ArrayList<Item> items = XMLParser.getItemsFromOnlineRSS(link);

        if(items != null)
        {
            for(int i = 0; i<items.size(); ++i)
            {
                //if the news exist in database
                //News news = newsMapper.findByTitleAndDescription(items.get(i).getTitle(), items.get(i).getDescription());
                Document newsFromDB = newsCollection.find(new Document("link", items.get(i).getLink())).first();

                if(newsFromDB != null){

                    if(newsVotedByUser.contains(newsFromDB.get("_id")))
                    {
                        items.remove(i);
                        --i;
                    }
                }

            }


            String topSite = "";
            for(int i=0;i<sites.size(); ++i)
            {
                if(sites.get(i).getRssFeed().equals(link))
                {
                    topSite = sites.get(i).getName();
                    break;
                }
            }

            model.addAttribute("topSite", topSite);

            model.addAttribute("itemList", items);
        }

        model.addAttribute("sites", sites);
        model.addAttribute("userName", userFromMongoDB.getString("name"));


        return  "/news";
    }


   /* @RequestMapping(value = "h2-console")
    public String h2Console()
    {
        System.out.println("\nh2 - console...\n");
        return "/h2-console";
    }*/


   /*
    @RequestMapping(value = "/myvotes")
    String myvotes(Model model)
    {

        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        User user = userMapper.getByUsername(username);

        ArrayList<Evaluation> myEvaluations = (ArrayList<Evaluation>) evaluationMapper.getByIdUser(user.getId());

        ArrayList<News> myNewsList = new ArrayList<News>();

        for(Evaluation eval : myEvaluations)
        {
            //System.out.println("id_news = " + eval.getIdNews() + " note="+ eval.getUserNote());
            News mynews = newsMapper.findById(eval.getIdNews());
            mynews.setNotee(eval.getUserNote());

            myNewsList.add(mynews);
        }
        model.addAttribute("myNewsList", myNewsList);

        return "/myvotes";
    }*/
}
