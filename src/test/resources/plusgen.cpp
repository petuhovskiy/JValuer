#include "testlib.h"
#include <iostream>

using namespace std;

int main(int argc, char* argv[]) {
    registerGen(argc, argv, 1);
    ios_base::sync_with_stdio(0);
    int test = atoi(argv[1]);
    int x = rnd.next(-test, test);
	int y = rnd.next(-test, test);
	cout << x << " " << y << endl;
    return 0;
}
