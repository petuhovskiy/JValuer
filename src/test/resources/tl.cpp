#include <cstdio>
#include <iostream>

using namespace std;

int main() {
    ios_base::sync_with_stdio(0);
    long long x;
    cin >> x;
    long long mod = 1e9 + 7;
    long long res = 1;
    for (long i = 1; i <= x; i++) {
        res = (res * i) % mod;
    }
    cout << res << endl;
    return 0;
}
