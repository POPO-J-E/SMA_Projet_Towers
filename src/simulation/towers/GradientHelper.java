package simulation.towers;

import javafx.util.Pair;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by kifkif on 30/10/2017.
 */
public class GradientHelper {
    public static int[][] initGradient(int[][] gradient)
    {
        for(int i = 0; i < gradient.length; i++){
            for(int j = 0; j < gradient[i].length; j++){
                gradient[i][j] = Integer.MAX_VALUE;
            }
        }

        return gradient;
    }

    public static int[][] calcGradient(int[][] gradient, Dimension loc)
    {
        java.util.List<Pair<Integer, Dimension>> locs = new ArrayList<>();
        locs.add(new Pair<>(0, loc));

        while (!locs.isEmpty())
        {
            Pair<Integer, Dimension> pair = locs.get(0);
            Dimension curLoc = pair.getValue();
            locs.remove(0);
            int grad = pair.getKey();
            if(updateGradientAt(gradient, curLoc, grad))
            {
                grad++;
                locs.add(new Pair<>(grad, new Dimension((int)curLoc.getWidth()+1, (int)curLoc.getHeight())));
                locs.add(new Pair<>(grad, new Dimension((int)curLoc.getWidth()-1, (int)curLoc.getHeight())));
                locs.add(new Pair<>(grad, new Dimension((int)curLoc.getWidth(), (int)curLoc.getHeight()+1)));
                locs.add(new Pair<>(grad, new Dimension((int)curLoc.getWidth(), (int)curLoc.getHeight()-1)));
            }
        }

        return gradient;
    }

    public static boolean updateGradientAt(int[][] gradient, Dimension loc, int grad)
    {
        if(inMap(gradient, loc) && gradient[loc.width][loc.height] > grad)
        {
            gradient[loc.width][loc.height] = grad;
            return true;
        }
        return false;
    }

    private static boolean inMap(int[][] gradient, Dimension loc) {
        return loc.width < gradient.length && loc.width >= 0 && loc.height < gradient[0].length && loc.height >= 0;
    }

    public static List<Dimension> getBestsNear(int[][] gradient, List<Dimension> locs)
    {
        List<Dimension> bests = new ArrayList<>();
        int best = Integer.MAX_VALUE;

        for (Dimension loc : locs) {
            int grad = gradient[loc.width][loc.height];

            if(grad == best)
            {
                bests.add(loc);
            }
            else if(grad < best)
            {
                bests.clear();
                bests.add(loc);
                best = grad;
            }
        }

        return bests;
    }

    public static String gradientToString(int[][] gradient)
    {
        StringBuilder stringBuilder = new StringBuilder("\n");

        for(int j = 0; j < gradient[0].length; j++){
            for(int i = 0;  i< gradient.length; i++){
                stringBuilder.append(gradient[i][j]).append("|");
            }
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }
}
