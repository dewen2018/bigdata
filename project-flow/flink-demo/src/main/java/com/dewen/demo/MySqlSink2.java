// package com.dewen.demo;
//
// import com.rules.engine.vo.UserInfoVo;
// import org.apache.flink.configuration.Configuration;
// import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
//
// import java.sql.Connection;
// import java.sql.PreparedStatement;
// import java.sql.Timestamp;
// import java.util.List;
//
// public class MySqlSink2 extends RichSinkFunction<UserInfoVo> {
//
// //    @Autowired
// //    private JdbcTemplate jdbcTemplate;
//
//     private PreparedStatement ps;
//     private Connection connection;
//
//     @Override
//     public void open(Configuration parameters) throws Exception {
//         System.out.println("------MySqlSink2 open");
//         super.open(parameters);
//         //获取数据库连接，准备写入数据库
//         connection = DbUtils.getConnection();
//         String sql = "insert into user_info(id, name, deviceId, beginTime, endTime) values (?, ?, ?, ?, ?); ";
//         ps = connection.prepareStatement(sql);
//     }
//
//     @Override
//     public void close() throws Exception {
//         System.out.println("------MySqlSink2 close");
//         super.close();
//         //关闭并释放资源
//         if(connection != null) {
//             connection.close();
//         }
//
//         if(ps != null) {
//             ps.close();
//         }
//     }
//
//     @Override
//     public void invoke(UserInfoVo userInfo, Context context) throws Exception {
//         System.out.println("------MySqlSink2 invoke");
//         ps.setString(1, userInfo.getId());
//         ps.setString(2, userInfo.getDeviceId());
//         ps.setString(3, userInfo.getName());
//         ps.setTimestamp(4, new Timestamp(userInfo.getBeginTime()));
//         ps.setTimestamp(5, new Timestamp(userInfo.getEndTime()));
//         ps.addBatch();
//
//         //一次性写入
//         int[] count = ps.executeBatch();
//         System.out.println("--------666666 成功写入Mysql数量：" + count.length);
//
//     }
// }
