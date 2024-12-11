import PillModel as PillModel
import ImageContourCount as ImageContourCount
import sys
import shutil
import configparser
import pandas as pd
import datetime


class PillMain():

    def main(self, argv):
        if len(argv) != 3:
            print("Argument is wrong")
            print("Usage: python PillMain.py [IMAGE FULL PATH] [TEXT FILE PATH]")
            sys.exit()

        text_file_path = argv[2]

        data_info = pd.read_csv(text_file_path, delimiter='\t')
        ori_shape = data_info['shape'][0]
        drug_list_ori = data_info['drug_code'][0].replace('[','').replace(']','').replace(' ','').split(',')

        if drug_list_ori[0] == 'none':
            drug_list = drug_list_ori[0]
        else:
            drug_list = drug_list_ori

        nowdate = datetime.datetime.now().strftime('%y%m%d_%H%M%S')
        log_path = '/home/ubuntu/YAKSOKSERVER/yaksokServer/src/main/java/com/example/yaksokServer/image/log/'+nowdate+'.log'
        f=open(log_path,'a',encoding="utf-8")

        shape_list = ['circle', 'ellipse', 'triangle', 'diamond', 'pentagon', 'hexagon', 'octagon', 'square', 'etc']
        if ori_shape not in shape_list:
            print("SHAPE : circle, ellipse, triangle, diamond, pentagon, hexagon, octagon, square, and etc")
            sys.exit()

        image_path = argv[1]
        shape = ori_shape

        f.write(shape+'\n')
        f.write(image_path+'\n')

        # config file load for each shape
        config = configparser.ConfigParser()
        config_path = '/home/ubuntu/YAKSOKSERVER/predict/config/'

        if shape == 'circle':
            config_file = config_path + 'config_circle.ini'
        elif shape == 'ellipse':
            config_file = config_path + 'config_ellipse.ini'
        elif shape == 'triangle':
            config_file = config_path + 'config_triangle.ini'
        elif shape == 'diamond':
            config_file = config_path + 'config_diamond.ini'
        elif shape == 'pentagon':
            config_file = config_path + 'config_pentagon.ini'
        elif shape == 'hexagon':
            config_file = config_path + 'config_hexagon.ini'
        elif shape == 'octagon':
            config_file = config_path + 'config_octagon.ini'
        elif shape == 'square':
            config_file = config_path + 'config_square.ini'
        elif shape == 'etc':
            config_file = config_path + 'config_etc.ini'
        
        config.read(config_file, encoding='UTF-8')
        pillModel = PillModel.PillModel(config['pill_model_info']) 
        
        # image processing
        pillModel.pill_image_process(image_path)
        
        # image open
        img = pillModel.testImage(config['pill_model_info']['make_folder_path'])
        
        # model loading
        pillModel.pill_shape_conf(shape)
        pillModel.pill_model_loading(config['pill_model_info'])

        # prediction
        output = pillModel.pill_prediction(img)
        indices_top, includ_count = pillModel.pill_sorting(output, drug_list)

        sys.stdout.reconfigure(encoding='utf-8')
        print(pillModel.pill_information(indices_top))
        f.write(pillModel.pill_information(indices_top))
        f.close()

        # remove filter image folder
        shutil.rmtree(config['pill_model_info']['make_folder_path'])


if __name__ == '__main__':
    main_class = PillMain()
    main_class.main(sys.argv)
    
