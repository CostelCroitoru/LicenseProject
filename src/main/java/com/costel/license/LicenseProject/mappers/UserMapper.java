package com.costel.license.LicenseProject.mappers;


import org.apache.ibatis.annotations.*;
import tables.User;
import java.util.List;


@Mapper
public interface UserMapper {

    final String getAll = "SELECT * FROM USERS";
    final String getById = "SELECT * FROM USERS WHERE id = #{id}";
    final String getByUsername = "SELECT * FROM USERS WHERE username = #{username}";
    final String getIdMax = "SELECT MAX(id) FROM USERS";

    final String insert = "INSERT INTO USERS (id, name, username, password, role) VALUES (#{id}, #{name}, #{username}, #{password}, #{role})";
    final String delete = "DELETE FROM USERS";
    final String count = "SELECT COUNT (*) FROM USERS";

    @Select(getAll)
    @Results(value = {
            @Result(property="id", column="id"),
            @Result(property="name", column="name"),
            @Result(property="username", column="username"),
            @Result(property="password", column="password"),
            @Result(property="role", column="role")
    })
    public List<User> getAllUsers();

    @Select(getById)
    public User getById(@Param("id") int id);


    @Select(getByUsername)
    public User getByUsername(@Param("username") String username);

    @Select(getIdMax)
    public int getIdMax();


    @Insert(insert)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    public void insert(User user);

    @Select(count)
    public int getNumberUsers();

    @Delete(delete)
    public void delete();

}