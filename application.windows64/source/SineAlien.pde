class SineAlien extends Alien{
    int lastX;
    float dy = 0.01;
    public SineAlien(int x, int y, PImage img, int powerUp, ParticleHandler p, PImage b){
         super(x,y,img, powerUp,p, b);
         lastX = (int)x;
    }
   
    public void move(){
        y+= dy;
         x = (sin(y) * 10) + lastX;
    }
    
    
    
}