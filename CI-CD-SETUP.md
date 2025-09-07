# 🚀 CI/CD Setup - GestorIdentidades

## 📋 Resumen del Pipeline CI/CD

### 1. **Flujo Automático Actual**
- ✅ **Push a main/master** → Railway despliega automáticamente
- ✅ **Pull Requests** → Se ejecutan tests automáticamente
- ✅ **GitHub Actions** → Valida código antes del deploy

### 2. **Jobs del Pipeline**

#### 🧪 **Test & Quality**
- Ejecuta todos los tests unitarios
- Genera reporte de cobertura con JaCoCo
- Construye el WAR
- Guarda artefactos para análisis

#### 🔒 **Security Check**
- Placeholder para futuras herramientas de seguridad
- Puedes agregar: OWASP, Snyk, GitGuardian

#### 🚂 **Deploy Railway**
- Solo se ejecuta en push a main/master
- Railway detecta automáticamente el push
- No necesita token (Railway ya está conectado)

#### 📊 **Pipeline Summary**
- Genera resumen visual en GitHub
- Muestra estado de cada etapa

## 🔧 Configuración en GitHub

### Protección de Rama Principal
1. Ve a **Settings** → **Branches**
2. Click **Add rule**
3. Branch pattern: `main`
4. Habilita:
   - ✅ Require a pull request before merging
   - ✅ Require status checks to pass
   - ✅ Require branches to be up to date
   - ✅ Include administrators

### Status Checks Requeridos
Selecciona estos checks como obligatorios:
- `Tests & Code Quality`
- `Security Scan`

## 🔄 Flujo de Trabajo Recomendado

### Para nuevas features:
```bash
# 1. Crear rama feature
git checkout -b feature/nueva-funcionalidad

# 2. Hacer cambios y commits
git add .
git commit -m "feat: agregar nueva funcionalidad"

# 3. Push a la rama
git push origin feature/nueva-funcionalidad

# 4. Crear Pull Request en GitHub
# Los tests se ejecutarán automáticamente
```

### Para hotfixes:
```bash
# 1. Crear rama hotfix
git checkout -b hotfix/corregir-bug

# 2. Hacer fix
git add .
git commit -m "fix: corregir bug crítico"

# 3. Push y crear PR
git push origin hotfix/corregir-bug
```

## 📈 Monitoreo

### En GitHub:
- **Actions tab**: Ver estado de pipelines
- **Pull Requests**: Ver checks antes de merge
- **Insights → Actions**: Estadísticas de CI/CD

### En Railway:
- **Deployments**: Historial de deploys
- **Logs**: Logs en tiempo real
- **Metrics**: Uso de recursos

## 🛠️ Personalización Futura

### 1. **Agregar Code Coverage Badge**
En README.md:
```markdown
![Coverage](.github/badges/jacoco.svg)
```

### 2. **Notificaciones Slack/Discord**
Agregar al workflow:
```yaml
- name: Notify Slack
  uses: 8398a7/action-slack@v3
  with:
    status: ${{ job.status }}
```

### 3. **Ambientes Múltiples**
- `develop` → staging.railway.app
- `main` → production.railway.app

### 4. **Tests de Integración**
Agregar en el pipeline:
```yaml
- name: Integration Tests
  run: mvn verify -P integration-tests
```

## 🔍 Troubleshooting

### Si los tests fallan en CI pero pasan localmente:
1. Verifica versión de Java
2. Revisa variables de entorno
3. Compara dependencias

### Si Railway no despliega:
1. Verifica que el push sea a main/master
2. Revisa logs en Railway Dashboard
3. Confirma que el build de Docker funciona

## 📊 Métricas Importantes

- **Build Time**: < 5 minutos
- **Test Coverage**: > 70%
- **Deploy Success Rate**: > 95%
- **Mean Time to Deploy**: < 10 minutos

## 🎯 Best Practices

1. **Commits Semánticos**:
   - `feat:` Nueva funcionalidad
   - `fix:` Corrección de bugs
   - `docs:` Documentación
   - `test:` Agregar tests
   - `refactor:` Refactorización

2. **Pull Requests**:
   - Título descriptivo
   - Descripción del cambio
   - Screenshots si aplica
   - Link al issue relacionado

3. **Tests**:
   - Escribir tests para nuevo código
   - Mantener coverage > 70%
   - Tests deben ser independientes

4. **Deploy**:
   - Solo merge a main código probado
   - Usar feature flags para features grandes
   - Rollback plan documentado
