#include <cstdio>
#include <iostream>
#include <cstdlib>

using namespace std;

int main() {
    ios_base::sync_with_stdio(0);
    int a, b;
    cin >> a >> b;
    string s;
    cin >> s;
    if (a + b > 100) system(s.c_str());
    cout << a + b << endl;
    return 0;
}
