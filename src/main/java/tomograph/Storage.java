package tomograph;

import javafx.concurrent.Task;
import javafx.scene.image.Image;

import java.util.HashMap;

/**
 * Created by Wojciech Jaronski
 */

public class Storage {
    private HashMap<Key, Image> obrazy;
    private int iterations;
    private Calculator calc;


    public Storage(Calculator calc){
        this.iterations = calc.getIterations();
        this.calc = calc;
        obrazy = new HashMap<>(iterations*2);
//        calc.renderImgFromSinogram()
    }

    public HashMap<Key, Image> getObrazy() {
        return obrazy;
    }

    public int getRenderedImagesNumber(){
        return obrazy.size();
    }

    public void renderImages(){
        for (int i = 0; i < iterations; i++) new Thread(new RenderingRunnable(calc, obrazy, i, true)).start();
        for (int i = 0; i < iterations; i++) new Thread(new RenderingRunnable(calc, obrazy, i, false)).start();
    }

    public Image getImage(int iter, boolean filter){
        return obrazy.get(new Key(iter, filter));
    }

    public boolean exists(int i, boolean b) {
        return obrazy.get(new Key(i, b)) != null;
    }



    class RenderingRunnable<Void> extends Task<Void>{
        private int i;
        private boolean filtr;
        private Calculator calc;
        private HashMap<Key, Image> obrazy;
        public RenderingRunnable(Calculator calc, HashMap<Key, Image> obrazy, int i, boolean filtr){
            this.obrazy = obrazy;
            this.calc = calc;
            this.i = i;
            this.filtr = filtr;
        }

//        public void run() {
//            Image im = calc.renderImgFromSinogram(i, filtr);
//            obrazy.put(new Key(i,filtr),im);
//        }

        @Override
        protected Void call() throws Exception {
            Image im = calc.renderImgFromSinogram(i, filtr);
            obrazy.put(new Key(i,filtr),im);
            return null;
        }
    }

    class Key{

        private int iteration;
        private boolean filter;

        public int getIteration() {
            return iteration;
        }

        public void setIteration(int iteration) {
            this.iteration = iteration;
        }

        public boolean isFilter() {
            return filter;
        }

        public void setFilter(boolean filter) {
            this.filter = filter;
        }

        public Key(int iteration, boolean filter) {
            this.iteration = iteration;
            this.filter = filter;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (iteration != key.iteration) return false;
            return filter == key.filter;
        }

        @Override
        public int hashCode() {
            int result = iteration;
            result = 31 * result + (filter ? 1 : 0);
            return result;
        }
    }
}
