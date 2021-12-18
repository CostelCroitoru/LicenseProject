package com.costel.license.LicenseProject.mappers;

import org.apache.ibatis.annotations.*;
import tables.News;

import java.util.List;

@Mapper
public interface NewsMapper {
    final String getAll = "SELECT * FROM NEWS";
    final String getById = "SELECT * FROM NEWS WHERE id = #{id}";
    final String getIdMax = "SELECT MAX(id) FROM NEWS";

    final String insert = "INSERT INTO NEWS (id, title, description, link) VALUES (#{id}, #{title}, #{description}, #{link})";
    final String getByTitleAndDescription = "SELECT * FROM NEWS WHERE title=#{title} AND description=#{description}";
    final String count = "SELECT COUNT (*) FROM NEWS";
    final String delete = "DELETE FROM NEWS";

    @Select(getAll)
    @Results(value = {
            @Result(property="id", column="id"),
            @Result(property="title", column="title"),
            @Result(property="description", column="description"),
            @Result(property="link", column="link")
    })
    public List<News> getAllNews();


    @Select(getIdMax)
    public int getIdMax();

    @Insert(insert)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(News news);


    @Select(getById)
    public News findById(@Param("id") int id);

    @Select(getByTitleAndDescription)
    public News findByTitleAndDescription(@Param("title") String title, @Param("description") String description);

    @Select(count)
    public int count();

    @Delete(delete)
    public void delete();
}
