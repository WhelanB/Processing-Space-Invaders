class Alien{
    public float x = 0;
    public float y = 0;
    float dx = 1;
    boolean dropsPowerUp;
    int startY = 0;
    int explodeTime = (int)random(10, 25) * 1000;
    int startTime;
    int explosionTime = 0;
    int direction = 1;
    PImage sprite;
    PImage particle = loadImage("particle.png");
    PImage bombSprite;
    Bomb myBomb;
    ParticleHandler particleHandler;
    AlienState state = AlienState.Strafe;
    
    public Alien(int x, int y, PImage img, int power, ParticleHandler p, PImage bomb){
        dropsPowerUp = power == 0;
        bombSprite = bomb;
        startTime = millis();
        this.x = x;
        this.y = y;
        sprite = img;
        particleHandler = p;
    }
    
    void dropBomb(){
         myBomb = new Bomb((int)x+(sprite.width/2),(int) y + sprite.height, bombSprite, this);   
    }
    
    public void draw(){
        if(state != AlienState.Exploded){
            if(myBomb != null)
                myBomb.draw();
            image(sprite, x, y); 
        }
        
    }
    
    public void removebomb(){
         myBomb = null;   
    }
    public void move(){
        
        dx+= 0.001f;
        if(this.y > 480){
             gameState = GameState.Lost;   
        }
        if(myBomb != null)
            myBomb.move();
        explosionTime = millis();
        if(explosionTime - startTime >= explodeTime && state != AlienState.Exploded && myBomb == null){
           dropBomb();
           explosionTime = millis()-explosionTime;
        }
        if(state == AlienState.Strafe){
          x+= dx * direction; 
        }
        if(state == AlienState.MoveDown){
             y+= dx;
             if((y - sprite.height) > startY){
                 y = startY + sprite.height;
                 state = AlienState.Strafe;
                 direction *= -1;
                 x+=dx * direction;
                 
             }
                 
             
        }
         if((x+sprite.width > SCREENX || x < 0 )&& state != AlienState.MoveDown && state != AlienState.Exploded){
             state = AlienState.MoveDown; 
             startY = (int)this.y;
         }
        
    }
    
    public void Explode(){
         explode.play();
         explode.rewind();
         if(dropsPowerUp){
              int rand = (int)random(0,2);
              pickups.add(new Pickup((int)x,(int)y,loadImage("powerup" + rand + ".png"), PowerUp.values()[rand+1]));   
         }
             
         state = AlienState.Exploded;   
         for(int i = 0; i < sprite.width/2; i++){
                for(int j = 0; j < sprite.height/2; j++){
                    color c = color(red(sprite.get(i*2,j*2)), green(sprite.get(i*2,j*2)), blue(sprite.get(i*2,j*2    )));
                        particleHandler.Register(new Particle((int)x + (i*2),(int)y + (j * 2),random(-2, 2), random(-2,2), c )); 
                }
        }
             
    }
    
    
}
    