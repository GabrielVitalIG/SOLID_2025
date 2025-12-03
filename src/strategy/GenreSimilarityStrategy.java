// java
package strategy;

import model.GenreTree;
import model.Movie;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class GenreSimilarityStrategy implements RecommendationStrategy {

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<Movie> recommend(GenreTree tree, String userId) {
        if (tree == null || userId == null) return Collections.emptyList();

        try {
            // 1) Récupérer les notes de l'utilisateur (méthodes candidates)
            Object ratingsObj = tryInvoke(tree,
                    new String[]{"getUserRatings", "getRatingsForUser", "getRatings"},
                    new Class[]{String.class},
                    new Object[]{userId});

            Map<Object, Number> userRatings = asMapOfNumber(ratingsObj);

            // 2) Construire un profil de genres pondéré par les notes
            Map<String, Double> genreWeights = new HashMap<>();
            Set<Object> ratedIds = new HashSet<>();

            if (userRatings != null) {
                for (Map.Entry<Object, Number> e : userRatings.entrySet()) {
                    Object key = e.getKey();
                    Number rating = e.getValue() == null ? 1.0 : e.getValue();
                    Movie ratedMovie = (key instanceof Movie) ? (Movie) key : findMovieById(tree, String.valueOf(key));
                    if (ratedMovie == null) continue;
                    ratedIds.add(extractMovieId(ratedMovie));
                    Collection<String> genres = extractGenres(ratedMovie);
                    double w = rating.doubleValue();
                    for (String g : genres) {
                        genreWeights.merge(g, w, Double::sum);
                    }
                }
            }

            // 3) Récupérer tous les films candidats
            Collection<Movie> allMovies = getAllMovies(tree);
            if (allMovies == null) return Collections.emptyList();

            // 4) Calculer un score par film basé sur la somme des poids de ses genres
            Map<Movie, Double> scores = new HashMap<>();
            for (Movie m : allMovies) {
                Object mid = extractMovieId(m);
                if (mid != null && ratedIds.contains(mid)) continue; // exclure déjà notés
                double score = 0.0;
                for (String g : extractGenres(m)) {
                    score += genreWeights.getOrDefault(g, 0.0);
                }
                // si aucun poids (user n'a pas d'historique) on peut laisser score 0
                scores.put(m, score);
            }

            // 5) Trier et retourner
            return scores.entrySet().stream()
                    .sorted(Map.Entry.<Movie, Double>comparingByValue(Comparator.reverseOrder())
                            .thenComparing(e -> safeTitle(e.getKey())))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            // En cas d'erreur, retour vide (sécurisé)
            return Collections.emptyList();
        }
    }

    // Helpers reflection / extraction

    private static Object tryInvoke(Object target, String[] methodNames, Class[] paramTypes, Object[] params) {
        for (String name : methodNames) {
            try {
                Method m = findMethod(target.getClass(), name, paramTypes);
                if (m != null) {
                    m.setAccessible(true);
                    return m.invoke(target, params);
                }
            } catch (IllegalAccessException | InvocationTargetException ignored) {
            }
        }
        return null;
    }

    private static Method findMethod(Class<?> cls, String name, Class[] paramTypes) {
        try {
            return cls.getMethod(name, paramTypes);
        } catch (NoSuchMethodException e) {
            // essayer méthodes déclarées (private/protected)
            try {
                return cls.getDeclaredMethod(name, paramTypes);
            } catch (NoSuchMethodException ex) {
                return null;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<Object, Number> asMapOfNumber(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Map) {
            Map map = (Map) obj;
            Map<Object, Number> out = new HashMap<>();
            for (Object k : map.keySet()) {
                Object v = map.get(k);
                if (v instanceof Number) out.put(k, (Number) v);
                else {
                    try {
                        out.put(k, Double.valueOf(String.valueOf(v)));
                    } catch (Exception ignored) {
                    }
                }
            }
            return out;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Collection<Movie> getAllMovies(GenreTree tree) {
        Object res = tryInvoke(tree, new String[]{"getAllMovies", "getMovies", "movies"}, new Class[]{}, new Object[]{});
        if (res == null) return null;
        if (res instanceof Collection) {
            Collection<?> c = (Collection<?>) res;
            List<Movie> movies = new ArrayList<>();
            for (Object o : c) if (o instanceof Movie) movies.add((Movie) o);
            return movies;
        }
        return null;
    }

    private static Movie findMovieById(GenreTree tree, String id) {
        Object res = tryInvoke(tree, new String[]{"findMovieById", "getMovieById", "movieById"}, new Class[]{String.class}, new Object[]{id});
        if (res instanceof Movie) return (Movie) res;
        // fallback: chercher dans all movies par id
        Collection<Movie> all = getAllMovies(tree);
        if (all == null) return null;
        for (Movie m : all) {
            Object mid = extractMovieId(m);
            if (mid != null && String.valueOf(mid).equals(id)) return m;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Collection<String> extractGenres(Movie m) {
        if (m == null) return Collections.emptyList();
        try {
            // méthodes communes possibles
            Method mg = findMethod(m.getClass(), "getGenres", new Class[]{});
            if (mg != null) {
                Object res = mg.invoke(m);
                if (res instanceof Collection) {
                    Collection<?> c = (Collection<?>) res;
                    List<String> out = new ArrayList<>();
                    for (Object o : c) out.add(String.valueOf(o));
                    return out;
                }
            }
            // autre candidate: getGenreNames, genres()
            mg = findMethod(m.getClass(), "getGenreNames", new Class[]{});
            if (mg != null) {
                Object res = mg.invoke(m);
                if (res instanceof Collection) {
                    Collection<?> c = (Collection<?>) res;
                    List<String> out = new ArrayList<>();
                    for (Object o : c) out.add(String.valueOf(o));
                    return out;
                }
            }
        } catch (Exception ignored) {
        }
        return Collections.emptyList();
    }

    private static Object extractMovieId(Movie m) {
        if (m == null) return null;
        try {
            Method mid = findMethod(m.getClass(), "getId", new Class[]{});
            if (mid != null) return mid.invoke(m);
            mid = findMethod(m.getClass(), "id", new Class[]{});
            if (mid != null) return mid.invoke(m);
            // fallback to toString
            return String.valueOf(m.toString());
        } catch (Exception ignored) {
            return String.valueOf(m.toString());
        }
    }

    private static String safeTitle(Movie m) {
        if (m == null) return "";
        try {
            Method t = findMethod(m.getClass(), "getTitle", new Class[]{});
            if (t != null) {
                Object res = t.invoke(m);
                return res == null ? "" : String.valueOf(res);
            }
        } catch (Exception ignored) {
        }
        return "";
    }
}