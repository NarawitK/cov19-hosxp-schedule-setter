package com.narawit.schedulesetter.services;
import javax.sql.DataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import org.mariadb.jdbc.MariaDbDataSource;

import com.narawit.schedulesetter.helper.DbProperties;

public class DataSourceFactory {
    public static DataSource getMariaDataSource() {
        Properties props = new Properties();
        MariaDbDataSource mdbDS = null;
        try{
            FileInputStream iStream = DbProperties.GetDbPropertiesAsInputStream();
        	props.load(iStream);
            mdbDS = getDataSourceFromFactory(props);
            iStream.close();
        }
        catch (IOException | SQLException e){
            e.printStackTrace();
        }
        
        return mdbDS;
    }
    
    private static MariaDbDataSource getDataSourceFromFactory(Properties props) throws SQLException{

        boolean isUseExternalDatasource = Boolean.parseBoolean(props.getProperty("USE_EXTERNAL_DATASOURCE"));
        MariaDbDataSource mds = new MariaDbDataSource();
        if(isUseExternalDatasource){
            mds.setUrl(getJDBCUrl(props,"EXTERNAL_DB_URL", "DB_NAME"));
        }
        else{
            mds.setUrl(getJDBCUrl(props, "INTERNAL_DB_URL", "DB_NAME"));
        }
        // In this case. Intranet and WAN use same credential.
        mds.setUser(props.getProperty("DB_USERNAME"));
        mds.setPassword(props.getProperty("DB_PASSWORD"));
        return mds;
    }
    private static String getJDBCUrl(Properties props, String dbUrlPropKey, String dbNamePropKey){
        final String jdbcStartString = "jdbc:mariadb://";
        String url = jdbcStartString + props.getProperty(dbUrlPropKey) + ":" + props.getProperty("DB_PORT","3306") + "/" + props.getProperty(dbNamePropKey) + props.getProperty("SESSION_VARS");
        return url;
    }
}