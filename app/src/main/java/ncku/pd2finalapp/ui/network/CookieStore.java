package ncku.pd2finalapp.ui.network;

import android.content.Context;
import android.content.SharedPreferences;

import java.net.HttpCookie;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CookieStore implements java.net.CookieStore {

    private static final String PREFERENCE_NAME = "ncku.pd2finalapp.cookies";
    //store form:
    //key: uri
    //stringset: [cookieName1:cookieVal1, ...]
    private final SharedPreferences preferences;
    public CookieStore(Context context) {
        preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void add(URI uri, HttpCookie httpCookie) {
        Set<String> newCookies = new HashSet<>(preferences.getStringSet(stringOf(uri), Collections.emptySet()));
        newCookies.removeIf(cookie -> cookie.split(":")[0].equals(httpCookie.getName()));
        newCookies.add(httpCookie.getName() + ":" + httpCookie.getValue());
        preferences.edit()
                .putStringSet(stringOf(uri), newCookies)
                .apply();
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        return preferences.getStringSet(stringOf(uri), Collections.emptySet())
                .stream()
                .map((string) -> {
                    String[] pair = string.split(":");
                    HttpCookie cookie = new HttpCookie(pair[0], pair[1]);
                    cookie.setDomain(uri.getHost());
                    cookie.setPath("/");
                    cookie.setHttpOnly(true);
                    cookie.setVersion(0);
                    return cookie;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<HttpCookie> getCookies() {
        return preferences.getAll()
                .values()
                .stream()
                .map(o -> (Set<String>) o)
                .flatMap(new Function<Set<String>, Stream<HttpCookie>>() {
                    @Override
                    public Stream<HttpCookie> apply(Set<String> strings) {
                        return strings.stream().map((string) -> {
                            String[] pair = string.split(":");
                            return new HttpCookie(pair[0], pair[1]);
                        });
                    }
                })
                .collect(Collectors.toList());

    }

    @Override
    public List<URI> getURIs() {
        return preferences.getAll()
                .keySet()
                .stream()
                .map(URI::create)
                .collect(Collectors.toList());
    }

    @Override
    public boolean remove(URI uri, HttpCookie httpCookie) {
        Set<String> newCookies = new HashSet<>(preferences.getStringSet(stringOf(uri), Collections.emptySet()));
        boolean success = newCookies.removeIf(cookie -> {
            String[] pair = cookie.split(":");
            return pair[0].equals(httpCookie.getName()) && pair[1].equals(httpCookie.getValue());
        });
        if (success) {
            preferences.edit()
                    .putStringSet(stringOf(uri), newCookies)
                    .apply();
        }
        return success;
    }

    @Override
    public boolean removeAll() {
        preferences.edit().clear().apply();
        return true;
    }

    private static String stringOf(URI uri) {
        return uri.getHost();
    }
}