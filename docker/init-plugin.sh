#!/bin/bash
set -e

PLUGIN_ZIP="/tmp/plugins/tibetan-analyzer-plugin-v1.0-es8.7+.zip"

if [ -f "$PLUGIN_ZIP" ]; then
    echo "Installing Tibetan Analyzer Plugin..."
    /usr/share/elasticsearch/bin/elasticsearch-plugin install --batch "file://$PLUGIN_ZIP"
    echo "Plugin installed successfully"
else
    echo "Warning: Plugin file not found at $PLUGIN_ZIP"
fi

