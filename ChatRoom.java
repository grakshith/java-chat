/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroom;

/**
 *
 * @author rahul
 */
public class ChatRoom {
   

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        //1.include login instance
        //2.include Chat instance
        ChatRoom chatroom1=new ChatRoom();
        
    }
    final Chat chat1;
    private final Login login1;

    public ChatRoom() {
        this.login1 = new Login();
        this.chat1 = new Chat();
    }
    
}
