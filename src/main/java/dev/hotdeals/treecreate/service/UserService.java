package dev.hotdeals.treecreate.service;

import dev.hotdeals.treecreate.model.User;
import dev.hotdeals.treecreate.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService
{
    @Autowired
    UserRepo userRepo;

    public List<User> fetchAll()
    {
        return userRepo.fetchAll();
    }

    public User fetchById(int id)
    {
        return userRepo.fetchById(id);
    }

    public boolean addUser(User user)
    {
        return userRepo.addUser(user);
    }

    public boolean deleteUser(int id)
    {
        return userRepo.deleteUser(id);
    }

    public User searchByUsername(String username)
    {
        return userRepo.searchByUsername(username);
    }
}