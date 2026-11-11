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
- La IP de la VM cambia (`.90`, `.92`, `.98`, `.85`): se fija con
  `export VM_HOST=192.168.1.85` sin editar `vmenv.sh`.
  La IP por defecto de la VM es `192.168.1.98` (antes `.90` y `.92`). Con una IP
  nueva, `vm.sh` se cuelga sin mostrar nada: usa `ssh -q` dentro de `expect`,
  que solo contesta a `password:`, así que la pregunta `yes/no` de la clave del
  host queda sin responder. Se diagnostica con `ssh -vv ...` (acaba en
  `Host key verification failed`) y se resuelve una vez con
  `ssh -o HostKeyAlgorithms=+ssh-rsa -o StrictHostKeyChecking=accept-new test@<ip> exit`
  (el `sshd` de la VM solo ofrece claves `ssh-rsa`; sin la opción contesta
  `no matching host key type found`). El 2026-11-10 la VM pasó a otra red:
  `172.20.10.11`.
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
- `vm-push.sh` solo envía los ficheros que difieren de `HEAD`: si se revierte un
  cambio en el Mac (por ejemplo un `println` de depuración) el fichero vuelve a
  ser igual a `HEAD` y **la VM se queda con la versión modificada**. Forzarlo con
  `tools/vm-push.sh -f <ruta relativa al repo>...`.
- `vm.sh` oculta las líneas que empiezan por dos espacios y `palabra=` (es el
  filtro para quitar la salida de `SET`): una prueba que imprima `  getText='x'`
  parece no imprimir nada. Usar otro prefijo.
- `ant.cmd compile` solo recompila los `.java` más nuevos que su `.class`. Una
  constante `static final int` se copia en las clases que la usan al compilar,
  así que tras cambiar `OS.java` hay que borrar el `.class` de esas clases
  (`tools/audit-constants.py` las detecta con las constantes mal escritas).
- Cortar con `TaskStop` o por tiempo agotado una orden `vm.sh` deja la sesión a
  medias en el `sshd` de la VM; con varias, el servidor deja de aceptar
  conexiones (`kex_exchange_identification: Connection closed by remote host`)
  y hay que reiniciar el `sshd` en la VM.
- La JVM de la VM es IBM Java 1.3.1 SR10 (Classic VM con JIT). Aborta con
  `Passed wrong C array to Release<type>ArrayElements` al liberar un arreglo de
  un solo elemento o con un puntero que no salió de él, y con una General
  Protection Fault (`EIP = 00000000`, `SYS1808`) si se usa un `jfieldID` nulo.
  Ver `doc/lessons.html`.
- `vm-pull.sh` sincroniza con `rsync --delete` y la carpeta `doc/` de la VM es
  antigua: un pull revierte y borra páginas de la documentación, también las
  ediciones sin commitear. Commitear antes, o traer solo la DLL y la `.lib`.
- `ant.cmd dll` no puede copiar la DLL si una prueba la tiene abierta
  (`SYS0032`, y aun así `BUILD SUCCESSFUL`): cerrar antes la ventana de la
  prueba. Un `cd` a una carpeta con espacios dentro de `vm.sh` falla
  (`SYS0003`) y lo que sigue se ejecuta en otra carpeta.
- Una sesión de `vm.sh` puede quedarse abierta aunque el programa ya haya
  terminado, y la salida se pierde. Más seguro: `java ... > t.out 2>&1` en una
  llamada y `type t.out` en otra.
- Si la VM no contesta (`vm.sh` termina con 255 sin imprimir nada), mirar con
  `nc -z -G 2 <ip> 22` en las IPs conocidas si cambió de dirección.
- Tras commitear páginas de `doc/`, enviarlas a la VM:
  `git ls-files doc | xargs tools/vm-push.sh -f` (en zsh `$F` no se parte en
  palabras).
- Auditorías: `tools/audit-constants.py <dir con los headers>`,
  `tools/audit-jni-fields.py` y `tools/audit-native-types.py` (ver `doc/lessons.html`).
- Si `vm.sh` no devuelve ni `ver` en 20-30 s, parar: más sesiones sobre un
  `sshd` saturado lo empeoran (pasó dos veces al lanzar bucles con varias
  pruebas y tiempos largos). Reiniciar `sshd` en la VM. Las baterías se
  ejecutan una prueba por llamada con `VM_TIMEOUT` de 60-100 s, tras un `ver`.
- Mientras una prueba abre ventanas reales en la parte automática, se ven
  destellos. PM no oculta una ventana colocada fuera de la pantalla (la lleva a
  la esquina superior izquierda); `SWT013_01` usa
  `-Dswt.dialog.invisible=true` (`DirectoryDialog.open()` no muestra el
  diálogo pero ejecuta su bucle). `-Dswt.test.time=true` en `SWT012_01`
  imprime los milisegundos de cada comprobación.
