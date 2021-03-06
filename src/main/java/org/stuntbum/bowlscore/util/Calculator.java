package org.stuntbum.bowlscore.util;

import org.stuntbum.bowlscore.domain.Average;
import org.stuntbum.bowlscore.domain.League;
import org.stuntbum.bowlscore.domain.LeagueScore;
import org.stuntbum.bowlscore.domain.Score;

import java.util.*;

/**
 * Created by mikko on 16/01/16.
 */
public class Calculator {

    public static ArrayList<Score> getAvgFromScores(List<Score> scores) {

        //List<Average> averages = new ArrayList<Average>();
        Map<Date, Average> tmp = new TreeMap<Date, Average>();

        for (Score s : scores) {
            if (tmp.containsKey(s.getDate())) {
                Average avg = tmp.get(s.getDate());
                avg.addTotal(s.getScore(), s.getFormattedDate());
            } else {
                Average newAvg = new Average();
                newAvg.addTotal(s.getScore(), s.getFormattedDate());
                tmp.put(s.getDate(), newAvg);
            }
        }


        Iterator<Average> iter = tmp.values().iterator();
        ArrayList<Score> tmpscore = new ArrayList<Score>();
/*
        while(iter.hasNext()) {
            Average a = iter.next();
            Score s = new Score(a.getAvg(), a.getDate());
            tmpscore.add(s);
        }
        */
        return tmpscore;
    }

    public static ArrayList<Average> getAvegares(List<Score> scores) {
        Map<Date, Average> tmp = new TreeMap<Date, Average>();

        for (Score s : scores) {
            if (tmp.containsKey(s.getDate())) {
                Average avg = tmp.get(s.getDate());
                avg.addScore(s);
            } else {
                Average newAvg = new Average();
                newAvg.addScore(s);
                tmp.put(s.getDate(), newAvg);
            }

        }

        Iterator<Average> iter = tmp.values().iterator();
        ArrayList<Average> tmpscore = new ArrayList<Average>();
        while(iter.hasNext()) {
            Average a = iter.next();
            tmpscore.add(a);
        }
        return tmpscore;
    }

    public static League generateLeague(List<Score> scores) {
        League l = generateEmptyLeague();
        if (scores == null || scores.isEmpty()) {
            return l;
        }

        String startDate = scores.get(0).getFormattedDate();
        String curDate = scores.get(0).getFormattedDate();
        int size = scores.size();
        int i = 0;

        while (i < scores.size()) {
            List<Score> dayScores = generateDayScores(scores, scores.get(i).getFormattedDate());
            calculateLeagueDay(dayScores, l);
            i += dayScores.size();
        }

        return l;
    }

    public static List<Score> generateDayScores(List<Score> scores, String day) {
        List<Score> dayScores = new ArrayList<>();
        for (int i = 0; i < scores.size(); i++){
            if (scores.get(i).getFormattedDate().equals(day)) {
                dayScores.add(scores.get(i));
            }
        }
        //System.out.println("Dayscores generated for day: " + day + ". Size: " + dayScores.size());
        return dayScores;

    }

    public static void calculateLeagueDay(List<Score> dayScores, League league) {

        //System.out.println("dayScores.size: " + dayScores.size());
        league.addNumberOfTimes();
        LeagueScore aku = league.getSingleScore("Aku");
        LeagueScore mikko = league.getSingleScore("Mikko");
        LeagueScore olli = league.getSingleScore("Olli");

        int akuBest, mikkoBest, olliBest;
        akuBest = mikkoBest = olliBest = 0;

        int akuTotal, mikkoTotal, olliTotal;
        akuTotal = mikkoTotal = olliTotal = 0;

        Stack akuScores = new Stack();
        Stack mikkoScores = new Stack();
        Stack olliScores = new Stack();

        for (int i = 0; i < dayScores.size(); i++) {
            int[] scoreArray = new int[dayScores.size() * 3];
            for (int j = 0 ; j < scoreArray.length; j++) {
                scoreArray[j] = 0;
            }

            Score ds = dayScores.get(i);
            if (ds.getName().equals("Aku")) {
                akuTotal += ds.getScore();
                akuScores.push(ds.getScore());
                if (ds.getScore() > akuBest) {
                    akuBest = ds.getScore();
                }
            }
            if (ds.getName().equals("Mikko")) {
                mikkoTotal += ds.getScore();
                mikkoScores.push(ds.getScore());
                if (ds.getScore() > mikkoBest) {
                    mikkoBest = ds.getScore();
                }
            }
            if (ds.getName().equals("Olli")) {
                olliTotal += ds.getScore();
                olliScores.push(ds.getScore());
                if (ds.getScore() > olliBest) {
                    olliBest = ds.getScore();
                }
            }
        }

        calculateScores(akuScores, mikkoScores, olliScores, league);

        if (akuTotal >= mikkoTotal && akuTotal >= olliTotal) {
            aku.addBestTotal();
        }
        if (mikkoTotal >= akuTotal && mikkoTotal >= olliTotal) {
            mikko.addBestTotal();
        }
        if (olliTotal >= akuTotal && olliTotal >= mikkoTotal) {
            olli.addBestTotal();
        }

        if (akuBest >= mikkoBest && akuBest >= olliBest) {
            aku.addBestScore();
        }
        if (mikkoBest >= akuBest && mikkoBest >= olliBest) {
            mikko.addBestScore();
        }
        if (olliBest >= akuBest && olliBest >= mikkoBest) {
            olli.addBestScore();
        }
        //System.out.println("LeagueDay: " + league.toString());

    }

    protected static void calculateScores(Stack akuScores, Stack mikkoScores, Stack olliScores, League league) {

        while (true) {
            league.addNumberOfSeries();
            int aku = akuScores.empty() ? 0 : Integer.parseInt(""+akuScores.pop());
            int mikko = mikkoScores.empty() ? 0 : Integer.parseInt(""+mikkoScores.pop());
            int olli = olliScores.empty() ? 0 : Integer.parseInt(""+olliScores.pop());

            if (aku >= mikko && aku >= olli) {
                league.getSingleScore("Aku").addRoundWin();
            }
            if (mikko >= aku && mikko >= olli) {
                league.getSingleScore("Mikko").addRoundWin();
            }
            if (olli >= aku && olli >= mikko) {
                league.getSingleScore("Olli").addRoundWin();
            }
            if (akuScores.isEmpty() && mikkoScores.isEmpty() && olliScores.isEmpty()) {
                break;
            }
        }
    }

    public static League generateEmptyLeague() {
        League l = new League();
        LeagueScore aku = new LeagueScore("Aku");
        LeagueScore mikko = new LeagueScore("Mikko");
        LeagueScore olli = new LeagueScore("Olli");

        l.addScore(aku);
        l.addScore(mikko);
        l.addScore(olli);
        return l;
    }
}
