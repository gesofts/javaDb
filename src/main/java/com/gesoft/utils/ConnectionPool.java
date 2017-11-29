package com.gesoft.utils;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

public class ConnectionPool {

    private BoneCP m_boneCP = null;
    private String m_jdbcDriver;
    private String m_dbUrl;
    private String m_dbUsername;
    private String m_dbPassword;

    /**
     * 构造函数
     *
     * @param jdbcDriver String JDBC 驱动类串
     * @param dbUrl String 数据库 URL
     * @param dbUsername String 连接数据库用户名
     * @param dbPassword String 连接数据库用户的密码
     *
     */
    public ConnectionPool(String jdbcDriver, String dbUrl, String dbUsername, String dbPassword) {
        m_jdbcDriver = jdbcDriver;
        m_dbUrl = dbUrl;
        m_dbUsername = dbUsername;
        m_dbPassword = dbPassword;

    }

    /**
     *
     * 创建一个数据库连接池，连接池中的可用连接的数量采用类成员 initialConnections 中设置的值
     */
    public synchronized void createPool() throws Exception {
        Class.forName(m_jdbcDriver);
        BoneCPConfig config = new BoneCPConfig();
        config.setJdbcUrl(m_dbUrl);
        config.setUsername(m_dbUsername);
        config.setPassword(m_dbPassword);

        config.setMinConnectionsPerPartition(2);
        config.setMaxConnectionsPerPartition(20);

        config.setConnectionTimeout(1, TimeUnit.MINUTES);
        //设置连接空闲时间 
        config.setIdleMaxAgeInMinutes(2);
        //当连接池中的连接耗尽的时候 BoneCP一次同时获取的连接数
        config.setAcquireIncrement(2);
        //设置检查数据库中的空闲连接数的时间间隔
        config.setIdleConnectionTestPeriodInMinutes(1);
        //连接释放处理
        config.setReleaseHelperThreads(3);
        //设置分区  分区数为2 
        config.setPartitionCount(1);

        m_boneCP = new BoneCP(config);

    }

    /**
     * 通过调用 getFreeConnection() 函数返回一个可用的数据库连接 , 如果当前没有可用的数据库连接，并且更多的数据库连接不能创
     * 建（如连接池大小的限制），此函数等待一会再尝试获取。
     *
     * @return 返回一个可用的数据库连接对象
     */
    public synchronized Connection getConnection() throws SQLException {
        return m_boneCP.getConnection();
    }

    /**
     * 此函数返回一个数据库连接到连接池中，并把此连接置为空闲。 所有使用连接池获得的数据库连接均应在不使用此连接时返回它。
     *
     * @param 需返回到连接池中的连接对象
     */
    public void returnConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception ex) {
            }
        }
    }

    /**
     * 关闭连接池中所有的连接，并清空连接池。
     */
    public synchronized void closeConnectionPool() throws SQLException {
        m_boneCP.shutdown();
    }

    /**
     * 此函数返回一个数据库连接到连接池中，并把此连接置为空闲。 所有使用连接池获得的数据库连接均应在不使用此连接时返回它。
     *
     * @param 需返回到连接池中的连接对象
     */
    public void returnConnection(Connection conn, Statement st, ResultSet rs) {
        if (st != null) {
            safeCloseStatement(st);
        }
        if (rs != null) {
            safeCloseResultSet(rs);
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception ex) {
            }
        }
    }

    private void safeCloseStatement(Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException ex1) {

            }
        }
    }

    /**
     * 安全关闭ResultSet对象
     *
     * @param rs ResultSet对象
     */
    private void safeCloseResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex1) {

            }
        }
    }
}
