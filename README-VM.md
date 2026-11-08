# Eclipse SWT para OS/2 (PM) — flujo de trabajo VM ↔ Git

Este repositorio es la conversión a Git del SVN original de netlabs
(`https://svn.netlabs.org/repos/swt`, r1–r270), importado con `git svn`:

- `main` ← `svn/trunk`
- `svn/watcom_ant17_java141` ← `branches/watcom_ant17_java141`

El desarrollo y la compilación se hacen dentro de la VM eComStation
(`X:\apps\dev\swt\trunk`); este repo en el Mac es donde se versiona y se
publica en GitHub.

## Scripts (`../tools`)

| Script | Uso |
|---|---|
| `vm.sh '<cmd>'` | Ejecuta un comando CMD.EXE en la VM. Encadenar con `&`, p.ej. `./vm.sh 'X: & cd \apps\dev\swt\trunk & ant.cmd compile'` |
| `vm-pull.sh` | Empaqueta `trunk` en la VM, lo trae y lo sincroniza sobre este repo (`rsync --delete`). Luego revisar `git status` y commitear. |
| `vm-push.sh [REV]` | Envía a la VM los ficheros cambiados respecto de `REV` (por defecto `HEAD`) más los no versionados. |

Configuración en `tools/vmenv.sh` (host, usuario, ruta; sobrescribible por
variables de entorno `VM_HOST`, `VM_USER`, `VM_PASS`, `VM_SWT_DIR`).

## Ciclo habitual

1. Editar en la VM (o en el Mac y `vm-push.sh`).
2. Compilar/probar en la VM: `ant.cmd compile`, `ant.cmd dll`.
3. `vm-pull.sh` → `git diff` → `git commit`.

## Notas

- Los fuentes están en CRLF; `.gitattributes` (`* -text`) evita conversiones.
- `ant.cmd dll` informa `BUILD SUCCESSFUL` aunque falle `nmake`: revisar la
  salida de VisualAge C++ (`CPPC…`, `NMAKE : fatal error`).
- `ant.cmd` solo existe en la raíz de `trunk`; desde `tests\SWT` no se
  encuentra. `ant.cmd compile` ya compila también las pruebas.
- `dir /s` sobre `X:\` recorre todo el disco y tarda minutos: acotar siempre
  la ruta (`X:\apps\dev\...`). Los comandos `date /t` y `time /t` de OS/2
  esperan entrada y dejan `vm.sh` colgado.
- La IP por defecto de la VM es `192.168.1.98` (antes `.90` y `.92`). Con una IP
  nueva, `vm.sh` se cuelga sin mostrar nada: usa `ssh -q` dentro de `expect`,
  que solo contesta a `password:`, así que la pregunta `yes/no` de la clave del
  host queda sin responder. Se diagnostica con `ssh -vv ...` (acaba en
  `Host key verification failed`) y se resuelve una vez con
  `ssh -o StrictHostKeyChecking=accept-new test@<ip> exit`.
- En la sesión SSH `INCLUDE` y `LIB` salen vacíos; el entorno de compilación
  se define en otro sitio. El toolkit de eCS está en `C:\ECSTK` (`h`, `lib`);
  `X:\os2tk45` es una copia antigua (su `esplitter.h` no declara
  `TBInitialize`). Ver `doc/swt.html#toolkit.notes`.
- `ant.cmd dll` no recompila si `swtp2049.dll` está al día respecto de las
  fuentes. Comprobar la fecha con `dir` y, para forzar, borrar `swt.obj` y
  `swtp2049.dll` (guardar antes una copia `.bak`).
- Las pruebas visuales se lanzan desde una consola de la VM, no por SSH:
  `ant.cmd swt009_13`. Para una ejecución sin supervisión:
  `java -Djava.library.path=src\plugins\org.eclipse.swt.pm\os\os2\x86 -Dswt.test.autoclose=3000 -cp -classes- SWT009_13`
  desde `trunk` (cierra la ventana a los 3 s y muestra `PASSED`/`FAILED`).
  Vale para cualquier prueba que extienda `SWTTestCase`, incluidas las visuales
  originales (`SWT009_01` a `_12`).
- Publicar en GitHub desde Claude Code: `!` no tiene terminal, así que
  `git push` por HTTPS no puede pedir usuario/token. Hacerlo desde Terminal.app,
  o configurar una clave SSH.
