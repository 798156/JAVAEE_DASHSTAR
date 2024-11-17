package dashstar.handler;

import dashstar.model.User;
import dashstar.model.request.UserLoginRequest;
import dashstar.repository.UserRepository;
import dashstar.security.Secured;
import dashstar.util.BCryptUtil;
import dashstar.util.JwtUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/users")  // /api/users/*
public class UserHandler {

    @Inject
    private UserRepository userRepository;

    @GET
    @Path("/")  // /api/users
    @Secured({"user", "admin"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        List<User> users = userRepository.findAll();
        Map<String, Object> res = new HashMap<>();
        res.put("code", Response.Status.OK);
        res.put("data", users);
        return Response.status(Response.Status.OK).entity(res).build();
    }

    @GET
    @Path("/{id}")  // /api/users/{id}
    @Produces(MediaType.APPLICATION_JSON)
    public User getUserById(@PathParam("id") int id) {
        return userRepository.findByID(id);
    }

    @POST
    @Path("/login")  // /api/users/login
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(UserLoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            Map<String, Object> res = new HashMap<>();
            res.put("code", Response.Status.BAD_REQUEST);
            return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
        }
        if (!BCryptUtil.checkPassword(request.getPassword(), user.getPassword())) {
            Map<String, Object> res = new HashMap<>();
            res.put("code", Response.Status.BAD_REQUEST);
            res.put("msg", "wrong");
            return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
        }
        String token = JwtUtil.generateToken(user.getId());
        Map<String, Object> res = new HashMap<>();
        res.put("code", Response.Status.OK);
        res.put("token", token);
        res.put("data", user);
        return Response.status(Response.Status.OK).entity(res).build();
    }

    @POST
    @Path("/register")  // /api/users/register
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(User user) {
        user.setRole("user");
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            user.setRole("admin");
        }
        user.setPassword(BCryptUtil.hashPassword(user.getPassword()));
        userRepository.create(user);
        Map<String, Object> res = new HashMap<>();
        res.put("code", Response.Status.CREATED);
        return Response.status(Response.Status.CREATED).entity(res).build();
    }
}