$ErrorActionPreference = 'Stop'
$root = 'd:\Trae Work\kaipiao\invoicing-system-design'
$partials = Join-Path $root 'partials'
$pages    = Join-Path $root 'pages'

$utf8 = New-Object System.Text.UTF8Encoding($false)

$css = [System.IO.File]::ReadAllText((Join-Path $root 'colors_and_type.css'), $utf8)
$headTpl   = [System.IO.File]::ReadAllText((Join-Path $partials 'head.html'), $utf8)
$shellOpen = [System.IO.File]::ReadAllText((Join-Path $partials 'shell-open.html'), $utf8)
$shellClose= [System.IO.File]::ReadAllText((Join-Path $partials 'shell-close.html'), $utf8)
$cfgJson = [System.IO.File]::ReadAllText((Join-Path $root 'pages-config.json'), $utf8)
$cfg = $cfgJson | ConvertFrom-Json

$chartCdn = '<script src="https://unpkg.com/chart.js@4.4.1/dist/chart.umd.min.js"></script>'

$order = @('login','home','invoice-apply','invoice-records','manual-invoice')
foreach($k in $order){
  $c = $cfg.$k
  $body = [System.IO.File]::ReadAllText((Join-Path $partials $c.body), $utf8)

  $head = $headTpl.Replace('__CSS__', $css).Replace('__TITLE__', $c.title)
  if([bool]$c.charts){ $head = $head.Replace('__CHARTS_CDN__', $chartCdn) } else { $head = $head.Replace('__CHARTS_CDN__', '') }

  if($k -eq 'login'){
    $html = $head + "`n" + $body
  } else {
    $so = $shellOpen.Replace('__NAV_ACTIVE__', $c.nav).Replace('__PAGE_TITLE__', $c.title).Replace('__PAGE_SUBTITLE__', $c.subtitle)
    $scripts = ''
    if($c.scripts -ne ''){ $scripts = [System.IO.File]::ReadAllText((Join-Path $partials $c.scripts), $utf8) }
    $sc = $shellClose.Replace('__PAGE_SCRIPTS__', $scripts)
    $html = $head + "`n" + $so + $body + $sc
  }

  $out = Join-Path $pages "$k.html"
  [System.IO.File]::WriteAllText($out, $html, $utf8)
  Write-Output "assembled: $k.html ($($html.Length) bytes)"
}

Write-Output '--- placeholder check ---'
Get-ChildItem $pages -Filter *.html | ForEach-Object {
  $t = [System.IO.File]::ReadAllText($_.FullName, $utf8)
  $m = [regex]::Matches($t, '__(CSS|TITLE|CHARTS_CDN|NAV_ACTIVE|PAGE_TITLE|PAGE_SUBTITLE|PAGE_SCRIPTS)__')
  if($m.Count -gt 0){ Write-Output "  LEAK $($_.Name): $($m.Count)" } else { Write-Output "  OK   $($_.Name)" }
}
