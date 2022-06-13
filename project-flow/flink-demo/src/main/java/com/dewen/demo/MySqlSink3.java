// package com.dewen.demo;
//
// import com.rules.engine.entity.UserInfoCount;
// import com.rules.engine.vo.UserInfoVo;
// import org.apache.flink.configuration.Configuration;
// import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
// import org.apache.flink.streaming.api.functions.sink.SinkFunction;
//
// import java.sql.Connection;
// import java.sql.PreparedStatement;
// import java.sql.Timestamp;
//
// public class MySqlSink3 extends RichSinkFunction<UserInfoCount> {
//     private PreparedStatement ps;
//     private Connection connection;
//
//     @Override
//     public void open(Configuration parameters) throws Exception {
//         System.out.println("------MySqlSink3 open");
//         super.open(parameters);
//         //获取数据库连接，准备写入数据库
//         connection = DbUtils.getConnection();
//         String sql = "insert into user_info_count(userId, count) values (?, ?); ";
//         ps = connection.prepareStatement(sql);
//     }
//
//     @Override
//     public void close() throws Exception {
//         System.out.println("------MySqlSink3 close");
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
//     public void invoke(UserInfoCount userInfoCount, Context context) throws Exception {
//         System.out.println("------MySqlSink3 invoke");
// //        ps.setInt(1, 1);
//         ps.setString(1, userInfoCount.getUserId());
//         ps.setInt(2, userInfoCount.getCount());
//         ps.addBatch();
//
//         //一次性写入
//         int[] count = ps.executeBatch();
//         System.out.println("--------666666 成功写入Mysql数量：" + count.length);
//
//     }
// }
//
