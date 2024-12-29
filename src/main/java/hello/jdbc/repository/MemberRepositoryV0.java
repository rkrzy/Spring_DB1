package hello.jdbc.repository;

import java.util.NoSuchElementException;
import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;


@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException
    {
        String sql = "insert into member(member_id, money) values (?,?)";

        Connection con = null;
        PreparedStatement pstmt = null;


        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;
        }catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally {
           close(con, pstmt, null);
        }

    }
    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId=" + memberId);

            }
        } catch (SQLException e) {
            log.error("db error", e);
            return null;
        } finally {
            close(con, pstmt, rs);
        }

    }

    private void close(Connection con, Statement stmt, ResultSet rs)
    {
        if(rs != null)
        {
            try {
                rs.close();
            }
            catch (SQLException e)
            {
                log.info("error", e);
            }
        }
        if(stmt != null)
        {
            try{
                stmt.close(); // 이렇게 하면 여기서 에러가 터지면 아래쪽 If문에 영향을 주지는 않는다.
            }
            catch (SQLException e)
            {
                log.info("error", e);
            }
        }
        if(con != null)
        {
            try{
                con.close();
            }
            catch (SQLException e){
                log.info("error", e);
            }
        }
    }

    private Connection getConnection()
    {
        return DBConnectionUtil.getConnection();
    }
}
