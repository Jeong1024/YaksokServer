import ImageCrop as ImageCrop
import ImageFiltering as ImageFiltering
import Separate as Separate
import configparser
import sys
import time
import datetime


class PreprocessingMain():
    def __init__(self):
        self.config_file = 'preprocessing/preprocessing_config.ini'
    def main(self, argv):
        # ex) python3 PreProcessing.py circle preprocessing/preprocessing_config.ini
        # shape에 따라 config, argv 수정
        if len(argv) == 3:
            self.config_file = argv[2]
        
        shape = argv[1]
        self.shape = shape

        config = configparser.ConfigParser()
        config.read(self.config_file, encoding='UTF-8')

        self.ImgCrop = ImageCrop.ImageCrop(config['img_processing'])
        self.ImgFilter = ImageFiltering.ImageFiltering(config['img_processing'])
        self.Separate = Separate.Separate(config['img_processing'])
    
        start = time.time()

        # """ Image Crop """
        # self.ImgCrop.ImageProcess(shape)

        # """ Image Filtering """
        # self.ImgFilter.imgFiltering()
        
        """ Image Separte"""
        self.Separate.separateProcess()

        end = time.time()
        sec = end - start

        print('####### finish #######')
        result = str(datetime.timedelta(seconds=sec)).split(".")
        print(f'{main_class.shape} 수행 시간 : {result[0]}')


if __name__ == '__main__':
    main_class = PreprocessingMain()
    main_class.main(sys.argv)
        
