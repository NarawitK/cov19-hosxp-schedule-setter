package com.narawit.schedulesetter.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import com.narawit.schedulesetter.models.TreatmentPlanModel;

public class DataQuery {
    public static boolean updatePlanDate(TreatmentPlanModel obj) throws SQLException{
        final String sql = "UPDATE treatment_plan_type_schedule SET treatment_day_number = ? WHERE treatment_plan_type_schedule_id = ? AND treatment_plan_type_id = ?";
        DataSource ds = DataSourceFactory.getMariaDataSource();
        Connection con = ds.getConnection();
        int rowAffected = 0;
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, obj.getDay());
        stmt.setInt(2, obj.getPlanScheduleId());
        stmt.setInt(3, obj.getPlanTypeId());
        rowAffected = stmt.executeUpdate();
        con.close();
        return rowAffected > 0;
    }

    public static List<TreatmentPlanModel> getPlanData() throws SQLException{
        final String sql = "SELECT treatment_plan_type_id AS planId, " +
                "tpts.treatment_plan_type_schedule_id AS schedId, tpt.treatment_plan_type_name AS name, " +
                "tpts.treatment_day_number AS day, tpts.treatment_description AS description " +
                "FROM treatment_plan_type tpt " +
                "INNER JOIN treatment_plan_type_schedule tpts USING(treatment_plan_type_id) " +
                "WHERE tpts.treatment_number > 1";
        List<TreatmentPlanModel> dataList = new ArrayList<TreatmentPlanModel>();
        DataSource ds = DataSourceFactory.getMariaDataSource();
        Connection con = ds.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while(rs.next()){
        	TreatmentPlanModel obj = new TreatmentPlanModel(
        			rs.getInt("planId"),
        			rs.getInt("schedId"),
        			rs.getString("name"),
        			rs.getString("description"),
        			rs.getInt("day")
        			);
        	dataList.add(obj);
        }
        rs.close();
        stmt.close();
        con.close();
        return dataList;
    }
}