#include <cstdio>
#include <iostream>
#include <vector>

using namespace std;

int main() {
    ios_base::sync_with_stdio(0);
    long long t;
    cin >> t;
    vector<int> v(t, -1);
    v[t - 1] = 123;
    cout << v[t - 1] << endl << "hello";
    return 0;
}
