/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package session;

import java.sql.SQLException;

/**
 *
 * @author Akila Jayasinghe
 */
public class sessionCredential {
    
    private final String userName;
    private final String nickName;
    private final int userID;
    private final sessionCRUD databaseOperations = new sessionCRUD();
    
    public sessionCredential() {
        this.userName = "User";
        this.nickName = "User";
        this.userID = 0;
    }
    
    public sessionCredential(String userName) throws SQLException {
        this.userName = userName;
        this.nickName = databaseOperations.getNickName_by_userName(userName);
        this.userID = databaseOperations.getUserID_by_userName(userName);
    }
    
    public String getUserName() {
        return userName;
    }
    
    public String getNickName() {
        return nickName;
    }
    
    public int getUserID() {
        return userID;
    }
    
}
