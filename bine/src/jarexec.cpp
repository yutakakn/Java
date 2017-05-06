//---------------------------------------------------------------------------

/* 2001/2/25
 *
 * jarexec.cpp - Javaアプリケーションの起動を容易にするラッパー
 *                 with Borland C++Builder5
 *
 * Copyright (C)2001 YUTAKA
 * All rights reserved.
 */

#include <vcl.h>
#include <windows.h>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <io.h>

#pragma hdrstop

//---------------------------------------------------------------------------

#if 0
int checkCommand(char *path, char *cmd)
{
	int len;
	char *p;

	len = strlen(path) + strlen(cmd) + 2;
	p = (char *)malloc(len);
	sprintf(p, "%s\\%s", path, cmd);

	if (access(p, 0) == 0) { //コマンドが見つかった
		return 1;
	}

	free(p);
	return 0;
}

int searchPath(char *cmd)
{
	char *path, *ptr;

	path = getenv("PATH");
	ptr = strtok(path, ";");

	if (checkCommand(ptr, cmd)) {
		return 1;
	}

	while ((ptr = strtok(NULL, ";")) != NULL) {
		if (checkCommand(ptr, cmd)) {
			return 1;
		}
	}

	return 0;
}
#endif

int readConfigFile(char *jvm, char *target)
{
	char *filename = "j2run.ini";
	char buf[BUFSIZ], *p, *sp;
	FILE *fp;
	int len;

	*jvm = *target = NULL;

	fp = fopen(filename, "r");
	if (fp == NULL) {
		return 1;
	}

	while (fgets(buf, sizeof(buf), fp) != NULL) {
		len = strlen(buf);
		if (buf[len - 1] == '\n') {
			buf[len - 1] = 0;
		}
		p = buf;
		while ( *p == ' ' || *p == '\t' ) {
			p++;
		}
		if (*p == '#' || *p == '\n' || *p == 0)
			continue;

		if ((sp = strstr(p, "JVM=")) != NULL) {
			strcpy(jvm, sp + 4);
		} else if ((sp = strstr(p, "TARGET=")) != NULL) {
			strcpy(target, sp + 7);
		} else {
			//Application->MessageBox(p, "hoge", MB_OK);
		}
	}
	fclose(fp);

	if (*jvm == NULL || *target == NULL) {
		return 2;
	}

	return 0;
}


#pragma argsused
WINAPI WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR lpCmdLine, int nCmdShow)
{
	char buf[BUFSIZ];
	char jvm[BUFSIZ], target[BUFSIZ];
	int ret;

	ret = readConfigFile(jvm, target);
	if (ret == 0) {
		sprintf(buf, "%s %s", jvm, target);
		//Application->MessageBox(buf, "JARプログラムの起動（デバッグ）", MB_OK);
		WinExec(buf, nCmdShow);
	} else {
		sprintf(buf, "error %d", ret);
		Application->MessageBox(
			"設定ファイルの記述が間違っています",
			"設定エラー",
			MB_OK
		);
	}

	return 0;
}
//---------------------------------------------------------------------------
