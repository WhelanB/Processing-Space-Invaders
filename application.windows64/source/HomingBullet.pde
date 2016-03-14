public class HomingBullet extends Bullet{
    Alien target;
    public HomingBullet(int x,int y, PImage img, Player o){
        
        super(x,y,img,o);
        homing.play();
        homing.rewind();
        if(aliens.size() > 0){
            target = aliens.get((int)random(0,aliens.size()));
            while(target != null && target.state == AlienState.Exploded)
                target = aliens.get((int)random(0,aliens.size()));
        }
        
    }
    
    public void move(){
        if(gameState == GameState.Playing){
        ypos-=5;
       
        if(target != null){
                 if(this.xpos > target.x)
                     xpos-=5;
                 else
                     xpos+=5;
            }
            else
            {
                 if(this.xpos > SCREENX/2)
                     xpos-=5;
                 else
                     xpos+=5;
            }
            
        }
    }
}