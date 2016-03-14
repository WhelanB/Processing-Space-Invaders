import java.util.Iterator;
class ParticleHandler{
 
    ArrayList<Particle> particles = new ArrayList<Particle>();
    
    public ParticleHandler(){
        
    }
    
    public void Register(Particle p){
         particles.add(p);   
    }
    
    public void draw(){
         for(Iterator<Particle> p = particles.iterator(); p.hasNext();){
             Particle particle = p.next();
             particle.draw();
             if(particle.isInvisible()){
                p.remove();
             }
         }
          
    }
    
}