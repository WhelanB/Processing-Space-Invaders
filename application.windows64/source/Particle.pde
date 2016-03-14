public class Particle{
    float x;
    float y;
    float dx;
    float dy;
    float angle = 0;
    int alpha = 255;
    color sprite;
     public Particle(float x, float y, float dx, float dy, color img){
         this.x = x;
         this.y = y;
         this.dx = dx;
         this.dy = dy;
         sprite = img;
     }
     
     void draw(){
        alpha -= 5;
        x += dx;
        y += dy;
        dy+= 0.02;
        angle += 0.1f;
        pushMatrix();
        noStroke();
        translate(x,y);
        rotate(angle);
        fill(red(sprite), green(sprite), blue(sprite), alpha);
        rect(-2,-2,2,2);  
        popMatrix();
          
     }
     
     public boolean isInvisible(){
         return alpha < 0;
     }

}