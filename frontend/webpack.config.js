const path = require('path');
const { VueLoaderPlugin } = require('vue-loader');
const HtmlWebpackPlugin = require('html-webpack-plugin');

const isProduction = process.env.NODE_ENV === 'production';

const devServerHost = process.env.HOST || '0.0.0.0';
const devServerPort = Number(process.env.PORT || 9528);
const apiProxyTarget = process.env.API_PROXY_TARGET || 'http://localhost:8099';

module.exports = {
  entry: path.resolve(__dirname, 'src/main.js'),
  mode: isProduction ? 'production' : 'development',
  devtool: isProduction ? false : 'source-map',
  output: {
    path: path.resolve(__dirname, '../backend/src/main/resources/static/admin'),
    filename: isProduction ? 'js/[name].[contenthash:8].js' : 'js/[name].js',
    chunkFilename: isProduction ? 'js/[name].[contenthash:8].js' : 'js/[name].js',
    publicPath: '/admin/',
    clean: true
  },
  resolve: {
    extensions: ['.js', '.vue', '.json'],
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  module: {
    rules: [
      {
        test: /\.vue$/,
        loader: 'vue-loader'
      },
      {
        test: /\.js$/,
        exclude: /node_modules/,
        use: {
          loader: 'babel-loader'
        }
      },
      {
        test: /\.s?css$/,
        use: ['style-loader', 'css-loader', 'sass-loader']
      },
      {
        test: /\.(png|jpe?g|gif|svg|ico)$/i,
        type: 'asset/resource',
        generator: {
          filename: 'assets/[name].[hash:8][ext]'
        }
      }
    ]
  },
  plugins: [
    new VueLoaderPlugin(),
    new HtmlWebpackPlugin({
      template: path.resolve(__dirname, 'public/index.html'),
      filename: 'index.html',
      inject: 'body'
    })
  ],
  devServer: {
    host: devServerHost,
    port: devServerPort,
    hot: true,
    allowedHosts: 'all',
    open: false,
    client: {
      overlay: true
    },
    static: {
      directory: path.resolve(__dirname, 'public'),
      publicPath: '/admin/'
    },
    devMiddleware: {
      publicPath: '/admin/'
    },
    historyApiFallback: {
      rewrites: [
        { from: /^\/admin\/?$/, to: '/index.html' }
      ]
    },
    proxy: {
      '/api': {
        target: apiProxyTarget,
        changeOrigin: true
      }
    }
  }
};