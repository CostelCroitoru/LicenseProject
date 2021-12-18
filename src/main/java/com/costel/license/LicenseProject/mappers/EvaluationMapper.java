package com.costel.license.LicenseProject.mappers;

import org.apache.ibatis.annotations.*;
import tables.Evaluation;

import java.util.List;

@Mapper
public interface EvaluationMapper {
    final String getAll = "SELECT * FROM EVALUATION";
    final String getById = "SELECT * FROM EVALUATION WHERE id = #{id}";
    final String getByIdUser = "SELECT id_news, user_note FROM EVALUATION WHERE id_user = #{id_user}";
    final String getIdMax = "SELECT MAX(id) FROM EVALUATION";
    final String getByUserAndNews = "SELECT * FROM EVALUATION WHERE id_user=#{id_user} AND id_news=#{id_news}";
    final String getNotesCountByIdNewsGroupByUserNote = "SELECT COUNT(*), user_note FROM EVALUATION WHERE id_news=#{id_news} GROUP BY user_note";

    final String getNumberPositiveNotes = "SELECT COUNT(*) FROM EVALUATION WHERE id_news=#{id_news} AND user_note = 1";
    final String getNumberNegativeNotes = "SELECT COUNT(*) FROM EVALUATION WHERE id_news=#{id_news} AND user_note = 0";

    final String insert = "INSERT INTO EVALUATION (id, id_news, id_user, user_note, click_date, send_date)"
            + " VALUES "
            + "(#{id}, #{id_news}, #{id_user}, #{user_note}, #{click_date}, #{send_date})";

    final String count = "SELECT COUNT (*) FROM EVALUATION";
    final String delete = "DELETE FROM EVALUATION";

    @Select(getAll)
    @Results(value = {
            @Result(property="id", column="id"),
            @Result(property="ip_address", column="ip_address"),
            @Result(property="id_news", column="id_news"),
            @Result(property="id_user", column="id_user"),
            @Result(property="click_date", column="click_date"),
            @Result(property="send_date", column="send_date")
    })
    public List<Evaluation> getAllEvaluations();

    @Select(getById)
    public Evaluation getById(@Param("id") int id);

    @Select(getByIdUser)
    @Results(value = {
            @Result(property="id_news", column="id_news"),
            @Result(property="user_note", column="user_note")
    })
    public List<Evaluation> getByIdUser(@Param("id_user") int id_user);

    @Select(getNumberPositiveNotes)
    public int getNumberPositiveNotes(@Param("id_news") int id_news);

    @Select(getNumberNegativeNotes)
    public int getNumberNegativeNotes(@Param("id_news") int id_news);

    @Select(getIdMax)
    public int getIdMax();

    @Select(getByUserAndNews)
    public Evaluation getByUserAndNews(@Param("id_user") int id_user, @Param("id_news") int id_news);

    @Insert(insert)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    public void insert(Evaluation evaluation);

    @Select(count)
    public int count();

    @Delete(delete)
    public void delete();
}
